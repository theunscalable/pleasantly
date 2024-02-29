package com.theunscalable.pleasantly.dao

import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

data class ReadingItem(
    val id: Int,
    val title: String,
    val url: String,
    var completed: Boolean = false
)

class ReadingListDao(private val context: Context) {
    private val fileName = "reading_list.json"
    private val gson = Gson()

    @Synchronized
    fun saveReadingList(itemMap: Map<Int, ReadingItem>) {
        val jsonString = gson.toJson(itemMap.values)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    fun loadReadingList(): MutableMap<Int, ReadingItem> {
        try {
            context.openFileInput(fileName).use { stream ->
                val jsonString = stream.bufferedReader().use(BufferedReader::readText)
                val itemType = object : TypeToken<List<ReadingItem>>() {}.type
                val readingList : List<ReadingItem> = gson.fromJson(jsonString, itemType)
                val itemMap = mutableMapOf<Int, ReadingItem>()
                for (item in readingList) {
                    itemMap[item.id] = item
                }
                return itemMap
            }
        } catch (e: FileNotFoundException) {
            // If the file doesn't exist, return an empty list
            return mutableMapOf()
        }
    }
}