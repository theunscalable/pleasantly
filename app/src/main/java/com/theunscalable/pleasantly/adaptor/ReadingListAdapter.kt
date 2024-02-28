package com.theunscalable.pleasantly.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theunscalable.pleasantly.R
import com.theunscalable.pleasantly.dao.ReadingItem

class ReadingListAdapter(private val items: List<ReadingItem>) :
    RecyclerView.Adapter<ReadingListAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ReadingItem) {
            view.findViewById<TextView>(R.id.itemTitle).text = item.title
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
}
