package com.theunscalable.pleasantly.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theunscalable.pleasantly.R
import com.theunscalable.pleasantly.dao.ReadingItem
import com.theunscalable.pleasantly.dao.ReadingListDao

class ReadingListAdapter(private val context: Context) :
    RecyclerView.Adapter<ReadingListAdapter.ViewHolder>() {

    private var items: MutableList<ReadingItem> = mutableListOf()
    private var readingListDao: ReadingListDao = ReadingListDao(context)

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ReadingItem) {
            view.findViewById<TextView>(R.id.itemTitle).text = item.title
            view.findViewById<TextView>(R.id.itemUrl).text = item.url
            // You can also set an onClickListener here to handle item clicks
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.reading_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun getCurrentList(): List<ReadingItem> {
        return items
    }

    fun saveCurrentList() {
        readingListDao.saveReadingList(items)
    }

    fun addToCurrentList(title: String, url: String) {
        // Find the maximum ID in the current list and add 1 for the new ID
        val newId = if (items.isEmpty()) 1 else items.maxByOrNull { it.id }?.id?.plus(1) ?: 1
        val newItem = ReadingItem(newId, title, url)
        items.add(newItem)
    }
}