package com.shuyu.gsygithubappcompose.core.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shuyu.gsygithubappcompose.core.database.entity.CommitFileEntity

class Converters {
    @TypeConverter
    fun fromCommitFileList(value: List<CommitFileEntity>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toCommitFileList(value: String): List<CommitFileEntity>? {
        val listType = object : TypeToken<List<CommitFileEntity>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromString(value: String?): List<String?>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<String?>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String?>?): String? {
        if (list == null) {
            return null
        }
        return Gson().toJson(list)
    }
}
