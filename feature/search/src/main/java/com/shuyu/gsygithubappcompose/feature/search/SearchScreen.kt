package com.shuyu.gsygithubappcompose.feature.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.components.RepositoryItem
import com.shuyu.gsygithubappcompose.core.ui.components.UserItem
import com.shuyu.gsygithubappcompose.core.ui.components.toRepositoryDisplayData

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
    val isLoading by searchViewModel.isLoading.collectAsState()
    val error by searchViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text(text = stringResource(id = R.string.nav_search)) },
                showBackButton = true
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchViewModel.onSearchQueryChanged(it) },
                label = { Text(stringResource(id = R.string.search_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { searchViewModel.performSearch() }),
                trailingIcon = {
                    IconButton(onClick = { searchViewModel.performSearch() }, enabled = searchQuery.isNotBlank()) {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(id = R.string.nav_search))
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Button(
                    onClick = { searchViewModel.onSearchTypeChanged(SearchType.REPOSITORY); searchViewModel.performSearch() },
                    enabled = searchQuery.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (searchType == SearchType.REPOSITORY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(stringResource(id = R.string.search_repository))
                }
                Button(
                    onClick = { searchViewModel.onSearchTypeChanged(SearchType.USER); searchViewModel.performSearch() },
                    enabled = searchQuery.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (searchType == SearchType.USER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(stringResource(id = R.string.search_user))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.wrapContentSize())
            } else if (error != null) {
                Text(text = error ?: stringResource(id = R.string.error_unknown), color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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

enum class SearchType {
    REPOSITORY,
    USER
}
