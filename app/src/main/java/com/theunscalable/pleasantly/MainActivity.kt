package com.theunscalable.pleasantly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theunscalable.pleasantly.adaptor.ReadingListAdapter
import com.theunscalable.pleasantly.dao.ReadingListDao
import com.theunscalable.pleasantly.utils.readReadingListFromAssets

class MainActivity : AppCompatActivity() {

    private lateinit var readingListDao: ReadingListDao
    private lateinit var readingListAdapter: ReadingListAdapter
    private lateinit var readingListRecyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readingListDao = ReadingListDao(this)
        readingListAdapter = ReadingListAdapter(readingListDao.loadReadingList())

        viewManager = LinearLayoutManager(this)
        val myDataset = readReadingListFromAssets(this)
        val readingListRecyclerView = findViewById<RecyclerView>(R.id.readingListRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ReadingListAdapter(myDataset)
        }
    }

    override fun onPause() {
        super.onPause()
        // Save the reading list when the activity pauses
        readingListDao.saveReadingList(readingListAdapter.getCurrentList())
    }
}