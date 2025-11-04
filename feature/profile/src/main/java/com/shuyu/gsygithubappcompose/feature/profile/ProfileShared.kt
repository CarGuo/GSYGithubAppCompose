package com.shuyu.gsygithubappcompose.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.network.model.User
import com.shuyu.gsygithubappcompose.core.ui.components.AvatarImage
import com.shuyu.gsygithubappcompose.core.ui.components.EventItem
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.UserItem
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
) {
    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.user == null,
        error = uiState.error,
        retry = onRefresh
    ) {
        GSYPullRefresh(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            isLoadMore = uiState.isLoadingMore,
            onLoadMore = onLoadMore,
            hasMore = uiState.hasMore,
            itemCount = if (uiState.user?.type == "Organization") uiState.orgMembers?.size
                ?: 0 else uiState.userEvents?.size ?: 0,
            loadMoreError = uiState.loadMoreError
        ) {
            uiState.user?.let {
                item {
                    ProfileHeader(user = it)
                }
            }
            if (uiState.user?.type == "Organization") {
                uiState.orgMembers?.let {
                    items(it) { member ->
                        UserItem(user = member)
                    }
                }
            } else {
                uiState.userEvents?.let {
                    items(it) { event ->
                        EventItem(event = event)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar
                AvatarImage(
                    url = user.avatarUrl,
                    size = 80.dp,
                    modifier = Modifier.clip(CircleShape),
                    username = user.login
                )

                // User Info
                Column {
                    Text(
                        text = user.name ?: user.login,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = user.login,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Bio
            user.bio?.let { bio ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Location, Company, Email
            Spacer(modifier = Modifier.height(8.dp))
            user.location?.let {
                Text(
                    text = "📍 $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            user.company?.let {
                Text(
                    text = "🏢 $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Join date
            Spacer(modifier = Modifier.height(4.dp))
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val joinDate = try {
                val parsedDate = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US
                ).parse(user.createdAt ?: "")
                parsedDate?.let { dateFormat.format(it) } ?: user.createdAt?.split("T")[0]
            } catch (e: Exception) {
                e.printStackTrace()
                user.createdAt?.split("T")[0]
            }
            Text(
                text = stringResource(id = R.string.profile_joined, joinDate ?: ""),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat(
                label = stringResource(id = R.string.profile_repositories),
                count = user.publicRepos ?: 0
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp),
                color = MaterialTheme.colorScheme.outline
            )
            ProfileStat(
                label = stringResource(id = R.string.profile_followers), count = user.followers ?: 0
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp),
                color = MaterialTheme.colorScheme.outline
            )
            ProfileStat(
                label = stringResource(id = R.string.profile_following), count = user.following ?: 0
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.dynamic_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileStat(label: String, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label, style = MaterialTheme.typography.bodySmall, color = Color.White
        )
    }
}
