package project.dwa.activities

import android.app.AlertDialog
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import project.dwa.R
import project.dwa.models.ViewPlayerConnector
import project.dwa.models.Player
import kotlin.properties.Delegates
import kotlin.random.Random

class ScalableBoardActivity : AppCompatActivity() {
    private lateinit var NOTHING_STATE: Drawable.ConstantState

    private var boardSize by Delegates.notNull<Int>()
    private var elementsToWin by Delegates.notNull<Int>()
    private lateinit var boardElementsArray: Array<Array<ViewPlayerConnector>>
    private lateinit var playerArray: ArrayList<Player>
    private var currentPlayerArrayIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use the board layout
        setContentView(R.layout.scalable_board)

        // Get the set view
        val board: ConstraintLayout = findViewById(R.id.scalable_board_id)

        // Default state of a board field
        NOTHING_STATE = ResourcesCompat.getDrawable(resources, R.drawable.nothing, this.theme)?.constantState!!

        // Get the size of the board
        boardSize = intent.extras?.getInt("size")!!

        // Get how many symbols are needed to win
        elementsToWin = intent.extras?.getInt("elementsToWin")!!

        // Get the player information
        playerArray = intent.extras?.getParcelableArrayList("players")!!

        // Get the flow element of the board and set the maximum amout of elements to the board size
        val boardFlow: Flow = findViewById(R.id.scalable_board_flow_id)
        boardFlow.setMaxElementsWrap(boardSize)

        // Get the size of a single element of the board can take
        val elementSize = getElementSize()

        // Initialize the elements arrau
        boardElementsArray = Array(boardSize) { Array(boardSize) { ViewPlayerConnector() } }

        // Saved state is null only when the activity is started for the first time
        if (savedInstanceState == null) {
            for (row in 0 until boardSize) {
                for (column in 0 until boardSize) {
                    // Create a view with a new index and no attached player
                    val emptyBoardElementView = createBoardElement(View.generateViewId(), null, elementSize)

                    // Add the data to the array
                    boardElementsArray[row][column] = ViewPlayerConnector(emptyBoardElementView.id, null)

                    // Add the view as an element of the ConstraintLayout Flow
                    boardFlow.referencedIds += emptyBoardElementView.id

                    // And add it to the main view
                    board.addView(emptyBoardElementView)
                }
            }
        } else {
            // Get the array saved from the Bundle
            val parsingArray = savedInstanceState.getParcelableArrayList<ViewPlayerConnector>("states")!!
            // Make it into a 2 dim array
            val paredArray = parsingArray.chunked(boardSize)

            for (row in 0 until boardSize) {
                for (column in 0 until boardSize) {
                    // Same like when the bundle is null, but the Id and Player exist
                    val existingBoardElement = paredArray[row][column]
                    val existingBoardElementView = createBoardElement(existingBoardElement.viewId, existingBoardElement.player, elementSize)

                    boardElementsArray[row][column] = ViewPlayerConnector(existingBoardElementView.id, existingBoardElement.player)

                    boardFlow.referencedIds += existingBoardElementView.id

                    board.addView(existingBoardElementView)
                }
            }
        }

        // Last, set the first player name as the current player
        setCurrentPlayerName()
    }

    // Used to save board state between reloads
    override fun onSaveInstanceState(outState: Bundle) {
        // Because we cannot save 2dim array, we need to transform it into 1dim array
        val parsingArray = boardElementsArray.flatten()

        // And save it
        outState.putParcelableArrayList("states", ArrayList(parsingArray))

        super.onSaveInstanceState(outState)
    }

    private fun playerTab(view: View): Boolean {
        // Parse the view and find the current player
        val img = view as ImageView
        val currentPlayer: Player = playerArray[currentPlayerArrayIndex]

        // Only allow to place on free fields
        if (img.drawable.constantState == NOTHING_STATE) {
            // Assign the player to the field - we use normal loops to use labeled break
            for (row in boardElementsArray) {
                for (item in row) {
                    if (item.viewId == img.id) {
                        item.player = currentPlayer

                        // Set the players image to the element
                        view.setImageResource(currentPlayer.symbol)

                        // Check the win condition - we use it here so it only check after a successful placement
                        // We send the indexes too, in order to not search the array again
                        checkWinConditionForElement(currentPlayer, boardElementsArray.indexOf(row), row.indexOf(item))

                        // If placed, go to next player
                        setNextPlayer()

                        // Return true if placement was successful
                        return true
                    }
                }
            }
        }

        // Return false if it wasn't successful
        return false
    }

    private fun setNextPlayer() {
        // Add one and then module with the size of the array so we don't go over the index
        currentPlayerArrayIndex = ((currentPlayerArrayIndex + 1) % playerArray.size)

        // Set the players name
        setCurrentPlayerName()

        // Check if it's a machine
        checkIfMachinePlayer()
    }

    private fun setCurrentPlayerName() {
        // Find the view with player names and set the text
        val playerNameView: TextView = findViewById(R.id.round_player_name_id)
        playerNameView.text = playerArray[currentPlayerArrayIndex].name
    }

    // Check if the active player is a machine
    private fun checkIfMachinePlayer() {
        val possibleMachinePLayer = playerArray[currentPlayerArrayIndex]

        // Check if the player is a Machine
        if (possibleMachinePLayer.isMachine) {
            // If yes, find random column and row
            do {
                val randomCol = Random.nextInt(0, boardSize)
                val randomRow = Random.nextInt(0, boardSize)

                // If the view is empty, place the symbol and go out of loop
                val randomlyFoundView: ImageView = findViewById(boardElementsArray[randomCol][randomRow].viewId)
                if (playerTab(randomlyFoundView)) break
            } while (true)
        }
    }

    private fun checkWinConditionForElement(player: Player, rowIndex: Int, columnIndex: Int) {
        // Create the necessary variables
        // If the value is negative, give 0
        val rowStartIndex = (rowIndex - elementsToWin).coerceAtLeast(0)
        val rowEndIndex = (rowIndex + elementsToWin).coerceAtMost(boardSize)
        val columnStartingIndex = (columnIndex - elementsToWin).coerceAtLeast(0)
        val columnEndIndex = (columnIndex + elementsToWin).coerceAtMost(boardSize)

        // Search in row
        var numInRow = 0
        for (rowElementIndex in rowStartIndex until rowEndIndex) {
            // Check if the symbol also belong to the player
            if (player.equals(boardElementsArray[rowElementIndex][columnIndex].player)) {
                numInRow++
            } else {
                numInRow = 0
            }

            if (numInRow == elementsToWin) {
                showWinPopup()
            }
        }

        // Search in column
        var numInColumn = 0
        for (columnElementIndex in columnStartingIndex until columnEndIndex) {
            // Check if the symbol also belong to the player
            if (player.equals(boardElementsArray[rowIndex][columnElementIndex].player)) {
                numInColumn++
            } else {
                numInColumn = 0
            }

            if (numInColumn == elementsToWin) {
                showWinPopup()
            }
        }

        // Search in diagonal
        var numInDiagonal = 0
        var diagonalRowIndex = rowStartIndex
        var diagonalColumnIndex = columnStartingIndex
        do {
            if (player.equals(boardElementsArray[diagonalRowIndex][diagonalColumnIndex].player)) {
                numInDiagonal++
            } else {
                numInDiagonal = 0
            }

            if (numInDiagonal == elementsToWin) {
                showWinPopup()
            }

            diagonalRowIndex++
            diagonalColumnIndex++
        } while (diagonalRowIndex < rowEndIndex && diagonalColumnIndex < columnEndIndex)

        // Search in anti-diagonal
        var numInAntiDiagonal = 0
        var antiDiagonalRowIndex = rowStartIndex
        var antiDiagonalColumnIndex = columnEndIndex - 1
        do {
            if (player.equals(boardElementsArray[antiDiagonalRowIndex][antiDiagonalColumnIndex].player)) {
                numInAntiDiagonal++
            } else {
                numInAntiDiagonal = 0
            }

            if (numInAntiDiagonal == elementsToWin) {
                showWinPopup()
            }

            antiDiagonalRowIndex++
            antiDiagonalColumnIndex--
        } while (antiDiagonalRowIndex < rowEndIndex && antiDiagonalColumnIndex > columnStartingIndex)
    }

    private fun showWinPopup() {
        WinnerPopUpDialog(playerArray[currentPlayerArrayIndex].name).show(supportFragmentManager, "Winner-Popup")
    }

    private fun getElementSize(): Int {
        // Get the current display size (width and height)
        // Scale the height a bit so it looks better
        val height = (Resources.getSystem().displayMetrics.heightPixels * 0.93).toInt()
        val width = Resources.getSystem().displayMetrics.widthPixels

        // Get the smaller one and divide it by the number of elements for the size of each element
        return (minOf(height, width) / boardSize)
    }

    // Create an element of the board (ImageView) using provided data
    private fun createBoardElement(id: Int, player: Player?, elementSize: Int): ImageView {
        val testView = ImageView(this)
        testView.id = id

        // Max sizes cannot be bigger than the element size
        testView.maxWidth = elementSize
        testView.maxHeight = elementSize
        testView.adjustViewBounds = true

        // Add the symbol of the player (od nothing by default) and add the border
        testView.setImageResource(player?.symbol ?: R.drawable.nothing)
        testView.setBackgroundResource(R.drawable.layout_border)

        // Register a click and use the playerTab method
        testView.setOnClickListener { playerTab(it) }

        // Return the view
        return testView
    }
}