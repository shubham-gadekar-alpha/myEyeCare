package com.alpha.myeyecare.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromDaysSet(days: Set<String>?): String? {
        return Gson().toJson(days)
    }

    @TypeConverter
    fun toDaysSet(authorsString: String?): Set<String>? {
        if (authorsString.isNullOrEmpty()) return emptySet()
        val setType = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(authorsString, setType)
    }
}
