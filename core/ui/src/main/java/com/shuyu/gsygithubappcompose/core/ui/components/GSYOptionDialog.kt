package com.shuyu.gsygithubappcompose.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shuyu.gsygithubappcompose.core.common.R

@Composable
fun GSYOptionDialog(
    options: List<String>,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    val cancelText = stringResource(id = R.string.cancel)
    val newOptions = options + cancelText

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                newOptions.forEach { option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (option != cancelText) {
                                    onOptionSelected(option)
                                } else {
                                    onDismiss()
                                }
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
