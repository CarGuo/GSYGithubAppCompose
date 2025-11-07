
package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shuyu.gsygithubappcompose.core.common.R

/**
 * Markdown 输入弹窗
 *
 * @param dialogTitle 弹窗标题
 * @param content 默认内容
 * @param onDismissRequest 点击外部区域或返回键
 * @param onConfirm 确认回调
 */
@Composable
fun GSYMarkdownInputDialog(
    dialogTitle: String,
    content: String? = null,
    onDismissRequest: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var text by remember { mutableStateOf(TextFieldValue(content ?: "")) }

    val markdownActions = remember {
        listOf(
            MarkdownAction(Icons.Default.Title, "H1") { text.insert("# ") },
            MarkdownAction(Icons.Default.Title, "H2") { text.insert("## ") },
            MarkdownAction(Icons.Default.Title, "H3") { text.insert("### ") },
            MarkdownAction(Icons.Default.FormatBold, "B") { text.insert("****") },
            MarkdownAction(Icons.Default.FormatItalic, "I") { text.insert("**") },
            MarkdownAction(Icons.Default.FormatListBulleted, "UL") { text.insert("- ") },
            MarkdownAction(Icons.Default.FormatQuote, "Quote") { text.insert("> ") },
            MarkdownAction(Icons.Default.Code, "Code") { text.insert("``") },
            MarkdownAction(Icons.Default.Image, "IMG") { text.insert("![]()") },
            MarkdownAction(Icons.Default.Link, "Link") { text.insert("[]()") },
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.material3.MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = dialogTitle, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.issue_title_tip)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = { Text(stringResource(id = R.string.issue_content_tip)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items(markdownActions) { action ->
                        MarkdownActionButton(
                            action = action,
                            onClick = {
                                text = action.onAction(text)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(title.text, text.text) }) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

private data class MarkdownAction(
    val icon: ImageVector,
    val description: String,
    val onAction: (TextFieldValue) -> TextFieldValue
)

@Composable
private fun MarkdownActionButton(
    action: MarkdownAction,
    onClick: () -> Unit
) {
    Icon(
        imageVector = action.icon,
        contentDescription = action.description,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    )
}


private fun TextFieldValue.insert(insertion: String): TextFieldValue {
    val newText = text.replaceRange(selection.start, selection.end, insertion)
    val newSelection = when (insertion) {
        "****" -> TextRange(selection.start + 2)
        "**" -> TextRange(selection.start + 1)
        "``" -> TextRange(selection.start + 1)
        "![]()" -> TextRange(selection.start + 2)
        "[]()" -> TextRange(selection.start + 1)
        else -> TextRange(newText.length)
    }
    return this.copy(text = newText, selection = newSelection)
}
