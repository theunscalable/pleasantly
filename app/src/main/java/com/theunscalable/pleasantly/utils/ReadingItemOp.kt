package com.theunscalable.pleasantly.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theunscalable.pleasantly.dao.ReadingItem
import java.io.IOException

fun readReadingListFromAssets(context: Context): List<ReadingItem> {
    val jsonString: String
    try {
        jsonString = context.assets.open("reading_list.json").bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }
    val gson = Gson()
    val listType = object : TypeToken<List<ReadingItem>>() {}.type
    return gson.fromJson(jsonString, listType)
}
