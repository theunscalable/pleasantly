package com.theunscalable.pleasantly

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

    private fun openAddToReadingListDialog(title: String, url: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_reading_item, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextUrl = dialogView.findViewById<EditText>(R.id.editTextUrl)

        // Pre-fill the dialog fields with the shared title and URL
        editTextTitle.setText(title)
        editTextUrl.setText(url)

        AlertDialog.Builder(this).apply {
            setView(dialogView)
            setTitle("Add to Reading List")
            setPositiveButton("Save") { _, _ ->
                val titleToSave = editTextTitle.text.toString().trim()
                val urlToSave = editTextUrl.text.toString().trim()
                readingListAdapter.addToCurrentList(titleToSave, urlToSave)
                readingListAdapter.saveCurrentList()
            }
            setNegativeButton("Cancel", null)
        }.create().show()
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
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
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
        findViewById<FloatingActionButton>(R.id.addItemFab).setOnClickListener {
            openAddToReadingListDialog("", "")
        }
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
            // Assuming the shared text is the URL, and EXTRA_SUBJECT might contain the title
            val sharedTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: ""
            // Open the dialog with the shared title and URL
            Log.d("MainActivity", "Shared text received: $sharedText")
            openAddToReadingListDialog(sharedTitle, sharedText)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Set the new intent
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
            // Assuming the shared text is the URL, and EXTRA_SUBJECT might contain the title
            val sharedTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: ""
            // Open the dialog with the shared title and URL
            Log.d("MainActivity", "Shared text received: $sharedText")
            openAddToReadingListDialog(sharedTitle, sharedText)
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