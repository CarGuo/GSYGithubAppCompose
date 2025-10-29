package com.shuyu.gsygithubappcompose.feature.login

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import android.webkit.WebResourceRequest

@Composable
fun OAuthWebView(
    url: String,
    onCodeReceived: (String) -> Unit,
    onError: () -> Unit
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val uri = request?.url
                        if (uri != null && uri.toString().startsWith("gsygithubapp://authed")) {
                            val code = uri.getQueryParameter("code")
                            if (code != null) {
                                onCodeReceived(code)
                            } else {
                                onError()
                            }
                            return true
                        }
                        return false
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(url)
            }
        }
    )
}
