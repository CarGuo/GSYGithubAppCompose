package com.shuyu.gsygithubappcompose.data.repository.vm

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest

/**
 * 通用屏幕封装，处理通用的 UI 逻辑，例如显示 Toast。
 *
 * @param VM ViewModel 的类型，必须继承自 BaseViewModel。
 * @param viewModel ViewModel 的实例。
 * @param content 页面的具体 Composable 内容。
 */
@Composable
fun <VM : BaseViewModel<*>> BaseScreen(
    viewModel: VM,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 监听来自 ViewModel 的 Toast 消息
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // 渲染页面的具体内容
    content()
}