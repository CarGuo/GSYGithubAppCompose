package com.shuyu.gsygithubappcompose.core.common.datastore

import androidx.annotation.StringRes
import com.shuyu.gsygithubappcompose.core.common.R

enum class AppLanguage(val languageCode: String, @StringRes val labelResId: Int) {
    SYSTEM("system", R.string.language_system),
    CHINESE("zh", R.string.language_chinese),
    ENGLISH("en", R.string.language_english);

    companion object {
        fun fromLanguageCode(languageCode: String?): AppLanguage {
            return values().find { it.languageCode == languageCode } ?: SYSTEM
        }
    }
}
