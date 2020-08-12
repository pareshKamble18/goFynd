package com.paresh.gofynd.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paresh.gofynd.model.Source

public class Converters_Source {
    @TypeConverter
    fun fromString(value: String?): Source {
        val listType = object : TypeToken<Source?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: Source?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}