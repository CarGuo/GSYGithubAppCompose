package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.common.R

@Composable
fun GSYGeneralLoadState(
    isLoading: Boolean,
    error: String?,
    retry: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
            error != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = retry) {
                        Text(stringResource(id = R.string.retry))
                    }
                }
            }
            else -> {
                content()
            }
        }
    }
}
