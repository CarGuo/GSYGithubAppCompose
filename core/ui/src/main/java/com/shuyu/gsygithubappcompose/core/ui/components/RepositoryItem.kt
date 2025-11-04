package com.shuyu.gsygithubappcompose.core.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.network.model.Repository
import com.shuyu.gsygithubappcompose.core.network.model.TrendingRepoModel

sealed interface RepoItemDisplayData {
    val fullName: String
    val description: String?
    val language: String?
    val starCount: String?
    val forkCount: String?
    val avatarUrl: String?
    val ownerName: String?
}

data class RepositoryDisplayData(
    override val fullName: String,
    override val description: String?,
    override val language: String?,
    override val starCount: String?,
    override val forkCount: String?,
    override val avatarUrl: String?,
    override val ownerName: String?
) : RepoItemDisplayData

data class TrendingDisplayData(
    override val fullName: String,
    override val description: String?,
    override val language: String?,
    override val starCount: String?,
    override val forkCount: String?,
    override val avatarUrl: String?,
    override val ownerName: String?
) : RepoItemDisplayData



@Composable
fun RepositoryItem(
    repoItem: RepoItemDisplayData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(
                    url = repoItem.avatarUrl ?: "",
                    size = 40.dp,
                    username = repoItem.ownerName,
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = repoItem.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    repoItem.language?.let { lang ->
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            repoItem.description?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = repoItem.starCount.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🔱",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = repoItem.forkCount.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


fun TrendingRepoModel.toTrendingDisplayData(): TrendingDisplayData {
    return TrendingDisplayData(
        fullName = fullName ?: "",
        description = description,
        language = language,
        starCount = starCount,
        forkCount = forkCount,
        avatarUrl = contributors?.firstOrNull() ?: "",
        ownerName = name
    )
}

fun Repository.toRepositoryDisplayData(): RepositoryDisplayData {
    return RepositoryDisplayData(
        fullName = fullName,
        description = description,
        language = language,
        starCount = stargazersCount.toString(),
        forkCount = forksCount.toString(),
        avatarUrl = owner.avatarUrl,
        ownerName = owner.login
    )
}