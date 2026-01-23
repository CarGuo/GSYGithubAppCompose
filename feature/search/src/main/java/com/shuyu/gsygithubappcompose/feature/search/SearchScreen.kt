package com.shuyu.gsygithubappcompose.feature.search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RepositoryItem
import com.shuyu.gsygithubappcompose.core.ui.components.UserItem
import com.shuyu.gsygithubappcompose.core.ui.components.toRepositoryDisplayData
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYSearchInput
import com.shuyu.gsygithubappcompose.core.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val navigator = LocalNavigator.current
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val searchType by searchViewModel.searchType.collectAsState()
    val repositoryResults by searchViewModel.repositoryResults.collectAsState()
    val userResults by searchViewModel.userResults.collectAsState()
    val isPageLoading by searchViewModel.isPageLoading.collectAsState()
    val error by searchViewModel.error.collectAsState()
    val isRefreshing by searchViewModel.isRefreshing.collectAsState()
    val isLoadingMore by searchViewModel.isLoadingMore.collectAsState()
    val hasMoreRepo by searchViewModel.hasMoreRepo.collectAsState()
    val hasMoreUser by searchViewModel.hasMoreUser.collectAsState()
    val loadMoreErrorRepo by searchViewModel.loadMoreErrorRepo.collectAsState()
    val loadMoreErrorUser by searchViewModel.loadMoreErrorUser.collectAsState()
    val searchHistory by searchViewModel.searchHistory.collectAsState(initial = emptyList<SearchHistoryEntity>())

    val focusManager = LocalFocusManager.current
    var isSearchFieldFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(searchViewModel.toastMessage) {
        searchViewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text(text = stringResource(id = R.string.nav_search)) },
                showBackButton = true
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            GSYSearchInput(
                value = searchQuery,
                onValueChange = { searchViewModel.onSearchQueryChanged(it) },
                onSearch = { searchViewModel.performSearch() },
                label = stringResource(id = R.string.search_hint),
                onFocusChanged = { isSearchFieldFocused = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isSearchFieldFocused && searchQuery.isBlank() && searchHistory.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.search_history),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = searchHistory) { historyItem: SearchHistoryEntity ->
                        Text(
                            text = historyItem.query,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchViewModel.onSearchQueryChanged(historyItem.query)
                                    searchViewModel.performSearch()
                                    focusManager.clearFocus()
                                }
                                .padding(vertical = 8.dp)
                        )
                        HorizontalDivider()
                    }
                }
            } else if (!isSearchFieldFocused || searchQuery.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            searchViewModel.onSearchTypeChanged(SearchType.REPOSITORY)
                            searchViewModel.performSearch()
                            focusManager.clearFocus()
                        },
                        enabled = searchQuery.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (searchType == SearchType.REPOSITORY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(stringResource(id = R.string.search_repository))
                    }
                    Button(
                        onClick = {
                            searchViewModel.onSearchTypeChanged(SearchType.USER)
                            searchViewModel.performSearch()
                            focusManager.clearFocus()
                        },
                        enabled = searchQuery.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (searchType == SearchType.USER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(stringResource(id = R.string.search_user))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (isPageLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Text(
                        text = error ?: stringResource(id = R.string.error_unknown),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    GSYPullRefresh(
                        onRefresh = { searchViewModel.refreshSearch() },
                        onLoadMore = { searchViewModel.loadNextPage() },
                        isRefreshing = isRefreshing,
                        isLoadMore = isLoadingMore,
                        hasMore = when (searchType) {
                            SearchType.REPOSITORY -> hasMoreRepo
                            SearchType.USER -> hasMoreUser
                        },
                        itemCount = when (searchType) {
                            SearchType.REPOSITORY -> repositoryResults.size
                            SearchType.USER -> userResults.size
                        },
                        loadMoreError = when (searchType) {
                            SearchType.REPOSITORY -> loadMoreErrorRepo
                            SearchType.USER -> loadMoreErrorUser
                        }
                    ) {
                        when (searchType) {
                            SearchType.REPOSITORY -> {
                                items(repositoryResults) {
                                    RepositoryItem(it.toRepositoryDisplayData())
                                }
                            }

                            SearchType.USER -> {
                                items(userResults) {
                                    UserItem(user = it) { user ->
                                        navigator.navigate("person/${user.login}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SearchType {
    REPOSITORY, USER
}