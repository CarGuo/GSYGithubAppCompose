package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colintheshots.twain.MarkdownText

@Composable
fun GSYMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    MarkdownText(
        markdown = markdown,
        modifier = modifier
    )
}
