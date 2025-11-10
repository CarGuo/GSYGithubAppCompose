package com.shuyu.gsygithubappcompose.core.ui.components

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.common.datastore.AppLanguage

@Composable
fun LanguageSelectDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val activity = LocalContext.current as Activity

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = com.shuyu.gsygithubappcompose.core.common.R.string.menu_language))
        },
        text = {
            Column {
                AppLanguage.values().forEach { language ->
                    LanguageOption(
                        language = language,
                        isSelected = currentLanguage == language,
                        onSelect = {
                            onLanguageSelected(language)
                            onDismissRequest()
                            activity.recreate() // Recreate the activity to apply language changes
                        }
                    )
                }
            }
        },
        confirmButton = {
            // No confirm button needed as selection dismisses the dialog
        }
    )
}

@Composable
fun LanguageOption(
    language: AppLanguage,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        Text(
            text = stringResource(id = language.labelResId),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
