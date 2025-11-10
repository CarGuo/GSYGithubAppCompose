package com.shuyu.gsygithubappcompose.feature.detail.info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuyu.gsygithubappcompose.core.network.model.RepositoryDetailModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailInfoHeader(
    modifier: Modifier = Modifier, repositoryDetailModel: RepositoryDetailModel
) {

    val navigator = LocalNavigator.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // User avatar as background with blur
            AsyncImage(
                model = repositoryDetailModel.ownerAvatarUrl,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(16.dp)
            )

            // Overlay to make text more readable
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // First line: User name (blue) and Repository name (white)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.clickable {
                            navigator.navigate("person/${repositoryDetailModel.owner}")
                        }, text = repositoryDetailModel.owner, style = TextStyle(
                            color = Color(0xFF2196F3), // Blue color
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, shadow = Shadow(
                                color = Color.Black, blurRadius = 8f
                            )
                        )
                    )
                    Text(
                        text = " / ", style = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black, blurRadius = 8f
                            )
                        )
                    )
                    Text(
                        text = repositoryDetailModel.name, style = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black, blurRadius = 8f
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second line: Language, Size, License
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repositoryDetailModel.languages?.apply {
                        Text(
                            text = this[0] ?: "", style = TextStyle(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                shadow = Shadow(
                                    color = Color.Black, blurRadius = 8f
                                )
                            )
                        )
                    }
                    repositoryDetailModel.size?.let { size ->
                        val sizeInKB = size / 1024.0
                        val formattedSize = if (sizeInKB < 1024) {
                            String.format(Locale.getDefault(), "%.2f KB", sizeInKB)
                        } else {
                            String.format(Locale.getDefault(), "%.2f MB", sizeInKB / 1024.0)
                        }
                        Text(
                            text = formattedSize, style = TextStyle(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                shadow = Shadow(
                                    color = Color.Black, blurRadius = 8f
                                )
                            )
                        )
                    }
                    repositoryDetailModel.license?.let {
                        Text(
                            text = it, style = TextStyle(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                shadow = Shadow(
                                    color = Color.Black, blurRadius = 8f
                                )
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Third line: Description
                repositoryDetailModel.shortDescriptionHTML?.let {
                    Text(
                        text = it, style = TextStyle(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            shadow = Shadow(
                                color = Color.Black, blurRadius = 8f
                            )
                        ), modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fourth and Fifth lines: Created At and Pushed At
                Column(
                    modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End
                ) {
                    val inputDateFormatIso8601WithMillis =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val inputDateFormatIso8601NoMillis =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                    val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    val createdAtDate: Date? = try {
                        inputDateFormatIso8601WithMillis.parse(repositoryDetailModel.createdAt)
                    } catch (e: Exception) {
                        try {
                            inputDateFormatIso8601NoMillis.parse(repositoryDetailModel.createdAt)
                        } catch (e2: Exception) {
                            null
                        }
                    }

                    if (createdAtDate != null) {
                        Text(
                            text = stringResource(
                                id = R.string.repo_detail_created,
                                outputDateFormat.format(createdAtDate)
                            ), style = TextStyle(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                shadow = Shadow(
                                    color = Color.Black, blurRadius = 8f
                                )
                            )
                        )
                    } else {
                        Text(
                            text = stringResource(
                                id = R.string.repo_detail_created, "Invalid Date"
                            ), style = TextStyle(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                shadow = Shadow(
                                    color = Color.Black, blurRadius = 8f
                                )
                            )
                        )
                    }

                    repositoryDetailModel.pushedAt?.let { pushedAtString ->
                        val pushedAtDate: Date? = try {
                            inputDateFormatIso8601WithMillis.parse(pushedAtString)
                        } catch (e: Exception) {
                            try {
                                inputDateFormatIso8601NoMillis.parse(pushedAtString)
                            } catch (e2: Exception) {
                                null
                            }
                        }

                        if (pushedAtDate != null) {
                            Text(
                                text = stringResource(
                                    id = R.string.repo_detail_last_commit,
                                    outputDateFormat.format(pushedAtDate)
                                ), style = TextStyle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    shadow = Shadow(
                                        color = Color.Black, blurRadius = 8f
                                    )
                                ), modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(
                                    id = R.string.repo_detail_last_commit, "Invalid Date"
                                ), style = TextStyle(
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    shadow = Shadow(
                                        color = Color.Black, blurRadius = 8f
                                    )
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(16.dp))

                // Stats: Star, Fork, Watch, Issue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RepositoryStatItem(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Stars",
                        count = repositoryDetailModel.stargazersCount
                    )
                    RepositoryStatItem(
                        imageVector = Icons.Filled.ForkRight,
                        contentDescription = "Forks",
                        count = repositoryDetailModel.forkCount
                    )
                    RepositoryStatItem(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "Watchers",
                        count = repositoryDetailModel.watchersCount
                    )
                    RepositoryStatItem(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Open Issues",
                        count = repositoryDetailModel.issuesTotal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(16.dp))

                // Topics
                repositoryDetailModel.topics?.let { topics ->
                    if (topics.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            topics.forEach { topic ->
                                CustomChip(
                                    text = topic ?: "",
                                    onClick = { /* Do nothing or navigate to topic search */ },
                                    backgroundColor = Color.Gray.copy(alpha = 0.5f),
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp, vertical = 1.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryStatItem(
    imageVector: ImageVector, contentDescription: String, count: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$count", style = TextStyle(
                color = Color.White, fontSize = 14.sp, shadow = Shadow(
                    color = Color.Black, blurRadius = 8f
                )
            )
        )
    }
}

@Composable
fun CustomChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text, style = TextStyle(
                    color = Color.White, fontSize = 14.sp, lineHeight = 14.sp, shadow = Shadow(
                        color = Color.Black, blurRadius = 4f
                    )
                )
            )
        }
    }
}
