package com.theunscalable.pleasantly.dao

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

data class ReadingItem(
    val id: Int,
    val title: String,
    val url: String
    // Add other fields as necessary
)
class ReadingListDao(private val context: Context) {
    private val fileName = "reading_list.json"
    private val gson = Gson()

    @Synchronized
    fun saveReadingList(items: List<ReadingItem>) {
        val jsonString = gson.toJson(items)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    fun loadReadingList(): List<ReadingItem> {
        try {
            context.openFileInput(fileName).use { stream ->
                val jsonString = stream.bufferedReader().use(BufferedReader::readText)
                val itemType = object : TypeToken<List<ReadingItem>>() {}.type
                return gson.fromJson(jsonString, itemType)
            }
        } catch (e: FileNotFoundException) {
            // If the file doesn't exist, return an empty list
            return emptyList()
        }
    }
}