package com.shuyu.gsygithubappcompose.feature.code

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.components.GSYTopAppBar
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FileCodeViewScreen(
    owner: String,
    repo: String,
    path: String,
    branch: String? = "main",
    viewModel: FileCodeViewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(owner, repo, path, branch) {
        viewModel.loadFile(owner, repo, path, branch)
    }

    Scaffold(
        topBar = {
            GSYTopAppBar(
                title = { Text(text = path.substringAfterLast('/')) }, showBackButton = true
            )
        }) { innerPadding ->
        GSYGeneralLoadState(
            isLoading = uiState.isPageLoading && uiState.content == null,
            error = uiState.error,
            retry = { viewModel.doInitialLoad() }) {
            GSYPullRefresh(
                modifier = Modifier.padding(innerPadding),
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                onLoadMore = { },
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
                        uiState.content?.let {
                            val baseUrl =
                                if (branch == null) "https://raw.githubusercontent.com/$owner/$repo/" else "https://raw.githubusercontent.com/$owner/$repo/$branch/"
                            val viewport =
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                            val isMd = path.lowercase().endsWith(".md")

                            val html = if (isMd) {
                                val style =
                                    "<style>" + "body{padding:15px;}" + "img{max-width:100% !important; height:auto !important;}" + "pre{background-color:#f6f8fa; padding:16px; overflow:auto; border-radius:6px;}" + "</style>"
                                "<html><head>$viewport$style</head><body>${it}</body></html>"
                            } else {
                                val fileType = path.substringAfterLast('.', "")
                                val langClass =
                                    if (fileType.isNotEmpty()) "class=\"language-$fileType\"" else ""
                                val highlightJsTheme =
                                    "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github-dark.min.css\">"
                                val highlightJsScript =
                                    "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script>"
                                val highlightJsInit = "<script>hljs.highlightAll();</script>"
                                val style =
                                    "<style>body{margin:0;} pre{margin:0;} code { padding: 16px !important; font-size: 14px !important; line-height: 1.5 !important; }</style>"

                                "<html><head>$viewport$highlightJsTheme$style</head>" + "<body><pre><code $langClass>${it}</code></pre>" + "$highlightJsScript$highlightJsInit</body></html>"
                            }
                            webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
                        }
                    })
                }
            }
        }
    }
}
