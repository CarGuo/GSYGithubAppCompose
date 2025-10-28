package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun AvatarImage(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = "Avatar",
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
        }
    )
}
