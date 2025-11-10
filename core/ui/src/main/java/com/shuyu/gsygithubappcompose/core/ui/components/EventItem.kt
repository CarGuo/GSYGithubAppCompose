package com.shuyu.gsygithubappcompose.core.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.network.model.Event
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun EventItem(
    event: Event
) {
    var showDialog by remember { mutableStateOf(false) }

    val navigator = LocalNavigator.current
    val context = LocalContext.current

    GSYCardItem(
        modifier = Modifier.clickable {
            val repoNameArray = event.repo.name.split("/")
            val owner = repoNameArray.getOrNull(0)
            val repoName = repoNameArray.getOrNull(1)

            when (event.type) {
                "IssueCommentEvent", "IssuesEvent" -> {
                    val issue = event.payload?.issue
                    if (issue != null && owner != null && repoName != null) {
                        navigator.navigate("issue_detail/${owner}/${repoName}/${issue.number}")
                    }
                }

                "PushEvent" -> {
                    val commits = event.payload?.commits
                    if (commits != null && commits.size > 1) {
                        showDialog = true
                    } else if (commits != null && commits.size == 1) {
                        val commit = commits[0]
                        if (owner != null && repoName != null) {
                            navigator.navigate(
                                "push_detail/${owner}/${repoName}/${commit.sha}"
                            )
                        }
                    } else if(event.payload != null && event.payload!!.head != null) {
                        navigator.navigate(
                            "push_detail/${owner}/${repoName}/${event.payload!!.head}"
                        )
                    }
                }

                "MemberEvent" -> {
                    navigator.navigate("person/${event.actor.login}")
                }

                "ReleaseEvent" -> {
                    val url = event.payload?.release?.tarballUrl
                    if (url != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                }

                else -> {
                    if (owner != null && repoName != null) {
                        navigator.navigate(
                            "repo_detail/${owner}/${repoName}"
                        )
                    }
                }
            }
        }
    ) {
        if (showDialog) {
            val commits = event.payload?.commits
            if (commits != null) {
                val commitMessages = commits.map {
                    it.message!!
                }
                GSYOptionDialog(
                    options = commitMessages,
                    onDismiss = {
                        showDialog = false
                    },
                    onOptionSelected = {
                        val commit = commits[it]
                        val repoNameArray = event.repo.name.split("/")
                        val owner = repoNameArray.getOrNull(0)
                        val repoName = repoNameArray.getOrNull(1)
                        if (owner != null && repoName != null) {
                            navigator.navigate(
                                "push_detail/${owner}/${repoName}/${commit.sha}"
                            )
                        }
                        showDialog = false
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            AvatarImage(
                url = event.actor.avatarUrl,
                username = event.actor.login,
                size = 40.dp,
                modifier = Modifier.clip(CircleShape)
            )

            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Username
                Text(
                    text = event.actor.login,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action description
                val actionText = when {
                    event.type.contains("Push") -> "pushed to"
                    event.type.contains("Create") -> "created"
                    event.type.contains("Fork") -> "forked"
                    event.type.contains("Star") -> "starred"
                    event.type.contains("Watch") -> "started watching"
                    event.type.contains("Issue") -> "created issue in"
                    event.type.contains("PullRequest") -> "created pull request in"
                    else -> event.type.replace("Event", "").lowercase()
                }

                Text(
                    text = "$actionText ${event.repo.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time
                val timeText = getRelativeTimeSpanString(event.createdAt)
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}