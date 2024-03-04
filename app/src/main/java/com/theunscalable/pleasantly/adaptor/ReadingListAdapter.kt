package com.theunscalable.pleasantly.adaptor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theunscalable.pleasantly.R
import com.theunscalable.pleasantly.dao.ReadingItem
import com.theunscalable.pleasantly.dao.ReadingListDao

class ReadingListAdapter(private val context: Context,
                         private val openUpdateDialog : (Int, String, String) -> Unit) :
    RecyclerView.Adapter<ReadingListAdapter.ViewHolder>() {

    private var readingListDao: ReadingListDao = ReadingListDao(context)
    private var itemMap: MutableMap<Int, ReadingItem> = readingListDao.loadReadingList()
    private var items: MutableList<ReadingItem> =
        itemMap.values.filter { !it.completed }.toMutableList()

    class ViewHolder(private val view: View,
                     clickAtPosition: (Int) -> Unit,
                     clickEditAtPosition: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val editButton: ImageView = view.findViewById(R.id.editButton)
        init {
            view.setOnClickListener {
                clickAtPosition(bindingAdapterPosition)
            }
            editButton.setOnClickListener {
                // Invoke a method in your adapter or use an interface to communicate with your activity/fragment
                clickEditAtPosition(bindingAdapterPosition)  // Assuming you have a `editListener` function defined
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
        fun clickAtPosition(position: Int) {
            val url = items[position].url
            openUrlInBrowser(url, context)
        }
        fun clickEditAtPosition(position: Int) {
            openUpdateDialog(position, items[position].title, items[position].url)
        }
        return ViewHolder(view, ::clickAtPosition, ::clickEditAtPosition)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun saveCurrentList() {
        readingListDao.saveReadingList(itemMap)
    }

    fun updateOrAddToCurrentList(itemPosition: Int, title: String, url: String) {
        if (itemPosition >= 0) {
            items[itemPosition].title = title
            items[itemPosition].url = url
            val id = items[itemPosition].id
            itemMap[id] = items[itemPosition]
            notifyItemChanged(itemPosition)
            return
        }
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