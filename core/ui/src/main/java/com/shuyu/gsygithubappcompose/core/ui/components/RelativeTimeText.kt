package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shuyu.gsygithubappcompose.core.common.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun RelativeTimeText(
    dateString: String,
    modifier: Modifier = Modifier
) {
    val relativeTime = getRelativeTimeSpanString(dateString)
    Text(
        text = relativeTime,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Composable
fun getRelativeTimeSpanString(dateString: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
    val date = sdf.parse(dateString)

    if (date == null) {
        return dateString
    }

    val now = Date()
    val diff = now.time - date.time

    val seconds = abs(diff) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    val years = days / 365

    return when {
        seconds < 60 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes)
        hours < 24 -> stringResource(R.string.time_hours_ago, hours)
        days < 30 -> stringResource(R.string.time_days_ago, days)
        months < 12 -> stringResource(R.string.time_months_ago, months)
        else -> stringResource(R.string.time_years_ago, years)
    }
}
