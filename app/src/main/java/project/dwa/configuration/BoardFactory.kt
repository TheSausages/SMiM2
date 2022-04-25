package project.dwa.configuration

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import project.dwa.R
import project.dwa.activities.ScalableBoardActivity
import project.dwa.models.Player
import project.dwa.models.ViewPlayerConnector

class BoardFactory {
    companion object BoardFactory {
        fun createEmptyBoard(boardSize: Int): Array<Array<ViewPlayerConnector>> {
            return Array(boardSize) { Array(boardSize) { ViewPlayerConnector(-1, null) } }
        }

        fun createBoardElement(
            context: Context,
            id: Int,
            elementSize: Int,
            onClick: (View) -> Unit,
            player: Player?
        ): ImageView {
            val testView = ImageButton(context)
            testView.id = id
            testView.tag = id

            // Max sizes cannot be bigger than the element size
            testView.maxWidth = elementSize
            testView.maxHeight = elementSize
            testView.adjustViewBounds = true

            // Add the symbol of the player (od nothing by default) and add the border
            testView.setImageResource(player?.symbol ?: R.drawable.nothing)
            testView.setBackgroundResource(R.drawable.layout_border)

            // Register a click and use the playerTab method
            testView.setOnClickListener { onClick(it) }

            // Return the view
            return testView
        }
    }
}