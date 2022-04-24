package project.dwa.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import project.dwa.R
import project.dwa.models.GameHistoryItem
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.charset.Charset


class WinnerPopUpDialog(val historyItem: GameHistoryItem): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return activity?.let {
            // Start building the Dialog
            val builder = AlertDialog.Builder(it)
            builder.setMessage(resources.getString(R.string.default_winner_text, historyItem.winner.name))
                // Button for saving the data
                .setPositiveButton(
                    R.string.save_and_go_to_menu_text
                ) { dialog, id ->
                    saveToHistory()

                    it.finish()
                }

                // Button for just returning to the menu
                .setNegativeButton(R.string.go_to_menu_text) { _, _ -> it.finish() }

            val dialog = builder.show()
            val messageText = dialog.findViewById<View>(android.R.id.message) as TextView
            messageText.gravity = Gravity.CENTER
            messageText.textSize = 30f
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveToHistory() {
        context?.let {
            try {
                val file = File(it.filesDir, GameHistoryItem.HISTORY_FILE_NAME)

                // If the file doesn't exist, create it
                file.exists().ifFalse { file.createNewFile() }

                // Create output stream to file and use append
                val fileOutputStream = FileOutputStream(file, true)

                // Write to file
                fileOutputStream.write(historyItem.toStringWithNewline().toByteArray(GameHistoryItem.WRITING_CHARSET))


                // Close the file stream
                fileOutputStream.close()
            } catch (e: Exception) {
                Toast.makeText(it, "Could not save to history", Toast.LENGTH_SHORT)
            }
        } ?: throw IllegalStateException("Activity Context cannot be null")
    }
}

fun <T> Boolean.ifTrue(action: () -> T): T? = if(this) action() else null

fun <T> Boolean.ifFalse(action: () -> T): T? = if(this) null else action()