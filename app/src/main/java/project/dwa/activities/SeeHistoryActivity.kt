package project.dwa.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.dwa.R
import project.dwa.adapters.HistoryListAdapter
import project.dwa.models.GameHistoryItem
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception

class SeeHistoryActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.history)
        val view: RecyclerView = findViewById(R.id.history_view_Id)

        view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        view.adapter = HistoryListAdapter(this, readFromHistory())
    }

    private fun readFromHistory(): List<GameHistoryItem> {
        this.let {
            try {
                val file = File(it.filesDir, GameHistoryItem.HISTORY_FILE_NAME)

                // If the file doesn't exist, create it
                if (file.exists()) {
                    // Create an Input Stream
                    val inputStream = FileInputStream(file)

                    // Create a reader for the stream
                    val fileReader = InputStreamReader(inputStream, GameHistoryItem.WRITING_CHARSET)

                    // Read all lines and map them to history items
                    val historyItems: List<GameHistoryItem> = fileReader
                        .readLines()
                        .map { line -> GameHistoryItem.fromString(line) }

                    // Close the file stream
                    inputStream.close()

                    //return the list
                    return historyItems
                } else {
                    // If no file exist, create it and return an empty list
                    file.createNewFile()

                    return ArrayList()
                }
            } catch (e: Exception) {
                Toast.makeText(it, "Could not read History", Toast.LENGTH_SHORT)
            }
        }
        return ArrayList()
    }
}