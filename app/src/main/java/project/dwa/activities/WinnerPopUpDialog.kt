package project.dwa.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import project.dwa.R


class WinnerPopUpDialog(val winnerName: String): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return activity?.let {
            // Start building the Dialog
            val builder = AlertDialog.Builder(it)
            builder.setMessage(resources.getString(R.string.default_winner_text, winnerName))
                // Button for saving the data
                .setPositiveButton(
                    R.string.save_and_go_to_menu_text
                ) { dialog, id ->
                    // TODO Save data
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
}