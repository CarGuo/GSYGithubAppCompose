package com.shuyu.gsygithubappcompose.feature.detail.readme

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RepoDetailReadmeScreen(
    owner: String,
    repo: String,
    branch: String? = "main",
    viewModel: RepoDetailReadmeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, repo) {
        viewModel.loadReadme(owner, repo, branch)
    }

    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.readme == null,
        error = uiState.error,
        retry = { viewModel.doInitialLoad() }) {
        GSYPullRefresh(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            onLoadMore = {},
            isLoadMore = false,
            hasMore = false,
            itemCount = 0
        ) {
            item {
                AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                    }
                }, update = { webView ->
                    uiState.readme?.let {
                        val baseUrl =
                            if (branch == null) "https://raw.githubusercontent.com/$owner/$repo/" else "https://raw.githubusercontent.com/$owner/$repo/$branch/"
                        val viewport =
                            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        val style =
                            "<style>" + "body{padding:15px;}" + "img{max-width:100% !important; height:auto !important;}" + "pre{background-color:#f6f8fa; padding:16px; overflow:auto; border-radius:6px;}" + "</style>"
                        val html = "<html><head>$viewport$style</head><body>${it}</body></html>"
                        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
                    }
                })
            }
        }
    }
}
