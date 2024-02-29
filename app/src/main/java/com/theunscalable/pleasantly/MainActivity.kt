package com.theunscalable.pleasantly

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.theunscalable.pleasantly.adaptor.ReadingListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var readingListAdapter: ReadingListAdapter
    private lateinit var readingListRecyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val handler = Handler(Looper.getMainLooper())
    private val saveRunnable = object : Runnable {
        override fun run() {
            readingListAdapter.saveCurrentList()
            // Reschedule the next execution
            handler.postDelayed(this, 15000) // 15 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readingListAdapter = ReadingListAdapter(this@MainActivity)

        val recyclerView = findViewById<RecyclerView>(R.id.readingListRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = readingListAdapter
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // This callback is not used in swipe actions, return false.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                readingListAdapter.markItemAsCompleted(position)
            }
        }

        // Attach the ItemTouchHelper to the RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        findViewById<FloatingActionButton>(R.id.addItemFab).setOnClickListener { view ->
            // Inflate the custom layout
            val dialogView =
                LayoutInflater.from(this).inflate(R.layout.dialog_add_reading_item, null)
            val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
            val editTextUrl = dialogView.findViewById<EditText>(R.id.editTextUrl)

            // Create and show the AlertDialog
            AlertDialog.Builder(this).apply {
                setView(dialogView)
                setTitle("Add to Reading List")
                setPositiveButton("Save") { _, _ ->
                    // Handle the save action
                    val title = editTextTitle.text.toString().trim()
                    val url = editTextUrl.text.toString().trim()
                    readingListAdapter.addToCurrentList(title, url)
                    readingListAdapter.saveCurrentList()
                }
                setNegativeButton("Cancel", null) // No additional action on cancel, just dismiss
            }.create().show()
        }
    }

    override fun onStop() {
        super.onStop()
        readingListAdapter.saveCurrentList()
        // Stop the periodic task to avoid memory leaks
        handler.removeCallbacks(saveRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Save the reading list when the activity pauses
        readingListAdapter.saveCurrentList()
    }
}