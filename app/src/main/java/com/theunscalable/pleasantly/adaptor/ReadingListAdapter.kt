package com.theunscalable.pleasantly.adaptor

import android.content.Context
import android.content.Intent
import android.net.Uri
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

    private var readingListDao: ReadingListDao = ReadingListDao(context)
    private var itemMap: MutableMap<Int, ReadingItem> = readingListDao.loadReadingList()
    private var items: MutableList<ReadingItem> = itemMap.values.filter { !it.completed }.toMutableList()

    class ViewHolder(private val view: View, clickAtPosition: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                clickAtPosition(bindingAdapterPosition)
            }
        }

        fun bind(item: ReadingItem) {
            view.findViewById<TextView>(R.id.itemTitle).text = item.title
            view.findViewById<TextView>(R.id.itemUrl).text = item.url
            // You can also set an onClickListener here to handle item clicks
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.reading_list_item, parent, false)
        return ViewHolder(view) { position ->
            val url = items[position].url
            openUrlInBrowser(url, context)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun saveCurrentList() {
        readingListDao.saveReadingList(itemMap)
    }

    fun addToCurrentList(title: String, url: String) {
        // Find the maximum ID in the current list and add 1 for the new ID
        val newId = if (items.isEmpty()) 1 else items.maxByOrNull { it.id }?.id?.plus(1) ?: 1
        val newItem = ReadingItem(newId, title, url)
        items.add(newItem)
        itemMap[newId] = newItem
        notifyItemInserted(items.size - 1)
    }

    private fun openUrlInBrowser(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun markItemAsCompleted(position: Int) {
        // Mark the item as completed
        itemMap[items[position].id]?.completed = true
        readingListDao.saveReadingList(itemMap)
        // Remove the item from the UI list
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}