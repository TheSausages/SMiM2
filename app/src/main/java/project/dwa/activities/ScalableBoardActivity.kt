package project.dwa.activities

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import project.dwa.R
import project.dwa.configuration.BoardFactory
import project.dwa.dialog.EndGamePopUp
import project.dwa.models.Board
import project.dwa.models.GameHistoryItem
import project.dwa.models.ViewPlayerConnector
import project.dwa.models.Player
import kotlin.properties.Delegates
import kotlin.random.Random

class ScalableBoardActivity : AppCompatActivity() {
    private lateinit var NOTHING_STATE: Drawable.ConstantState

    private lateinit var boardObj: Board
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
        val boardSize = intent.extras?.getInt("size")!!

        // Get how many symbols are needed to win
        val elementsToWin = intent.extras?.getInt("elementsToWin")!!

        // Get the player information
        playerArray = intent.extras?.getParcelableArrayList("players")!!

        // Get the flow element of the board and set the maximum amout of elements to the board size
        val boardFlow: Flow = findViewById(R.id.scalable_board_flow_id)
        boardFlow.setMaxElementsWrap(boardSize)

        // Initialize the elements array
        boardObj = Board(boardSize, elementsToWin)

        // Get the size of a single element of the board can take
        val elementSize = getElementSize()

        // Saved state is null only when the activity is started for the first time
        if (savedInstanceState == null) {
            for (row in 0 until boardSize) {
                for (column in 0 until boardSize) {
                    // Create a view with a new index and no attached player
                    val emptyBoardElementView = BoardFactory.createBoardElement(
                        this,
                        View.generateViewId(),
                        elementSize,
                        ::tabAndBeginNextRound,
                        null
                    )

                    // Add the data to the array
                    boardObj.boardElementsArray[row][column] = ViewPlayerConnector(emptyBoardElementView.id, null)

                    // Add the view to the board
                    board.addView(emptyBoardElementView)

                    // Add the view to the flow
                    boardFlow.addView(emptyBoardElementView)
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
                    val existingBoardElementView = BoardFactory.createBoardElement(
                        this,
                        existingBoardElement.viewId,
                        elementSize,
                        ::tabAndBeginNextRound,
                        existingBoardElement.player
                    )

                    boardObj.boardElementsArray[row][column] = ViewPlayerConnector(existingBoardElementView.id, existingBoardElement.player)

                    // Add the view to the board
                    board.addView(existingBoardElementView)

                    // Add the view to the flow
                    boardFlow.addView(existingBoardElementView)
                }
            }
        }

        // Last, set the first player name as the current player
        setCurrentPlayerInfo()
    }

    // Used to save board state between reloads
    override fun onSaveInstanceState(outState: Bundle) {
        // Because we cannot save 2dim array, we need to transform it into 1dim array
        val parsingArray = boardObj.boardElementsArray.flatten()

        // And save it
        outState.putParcelableArrayList("states", ArrayList(parsingArray))

        super.onSaveInstanceState(outState)
    }

    private fun tabAndBeginNextRound(view: View) {
        if (playerTab(view)) {
            setNextPlayer()
        }
    }

    private fun playerTab(view: View): Boolean {
        // Parse the view and find the current player
        val img = view as ImageButton
        val currentPlayer: Player = playerArray[currentPlayerArrayIndex]

        // Only allow to place on free fields
        if (img.drawable.constantState == NOTHING_STATE) {
            // Assign the player to the field - we use normal loops to use labeled break
            for (row in boardObj.boardElementsArray) {
                for (item in row) {
                    if (item.viewId == img.id) {
                        item.player = currentPlayer

                        // Set the players image to the element
                        view.setImageResource(currentPlayer.symbol)

                        // Check the win condition - we use it here so it only check after a successful placement
                        // We send the indexes too, in order to not search the array again
                        if (boardObj.checkWinCondition(currentPlayer, boardObj.boardElementsArray.indexOf(row), row.indexOf(item))) {
                            showWinPopup(resources.getString(R.string.default_winner_text, playerArray[currentPlayerArrayIndex].name))
                        }

                        // Check fi draw
                        boardObj.numberOfPlaced++
                        if (boardObj.checkIfDraw()) {
                            showDrawPopUp()
                        }

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
        goToNextPlayer()

        // if machine, do it's round
        ifMachinePlayRound()

        // Set the players name
        setCurrentPlayerInfo()
    }

    private fun setCurrentPlayerInfo() {
        val currentPlayer: Player = playerArray[currentPlayerArrayIndex]

        // Find the view with player names and set the text
        val playerNameView: TextView = findViewById(R.id.round_player_name_id)
        playerNameView.text = currentPlayer.name

        // Find the view with player number of wins and set the text
        val playerWinsView: TextView = findViewById(R.id.round_of_player_wins_text_id)
        playerWinsView.text = resources.getString(R.string.number_of_wins_text, currentPlayer.winsCounter)
    }

    // Check if the active player is a machine
    private fun ifMachinePlayRound() {
        val possibleMachinePlayer = playerArray[currentPlayerArrayIndex]

        // Check if the player is a Machine
        if (possibleMachinePlayer.isMachine) {
            // If yes, find random column and row
            do {
                val randomCol = Random.nextInt(0, boardObj.boardSize)
                val randomRow = Random.nextInt(0, boardObj.boardSize)

                // If the view is empty, place the symbol and go out of loop
                val randomlyFoundView: ImageButton = findViewById(boardObj.boardElementsArray[randomCol][randomRow].viewId)
                if (playerTab(randomlyFoundView)) break
            } while (true)

            // If Machine did it's round, go to next
            goToNextPlayer()
        }
    }


    internal fun showWinPopup(message: String) {
        val winner = playerArray[currentPlayerArrayIndex]

        winner.winsCounter++

        val playerArrayCopy = ArrayList(playerArray)

        playerArrayCopy.remove(winner)

        showPopUp(message, GameHistoryItem(winner, playerArrayCopy), "Winner-Popup")
    }

    private fun showDrawPopUp() = showPopUp(resources.getString(R.string.default_draw_text), GameHistoryItem(null, playerArray), "Draw-Popup")

    private fun showPopUp(message: String, historyItem: GameHistoryItem, tag: String) {
        EndGamePopUp(
            historyItem,
            message
        ).show(supportFragmentManager, tag)

        clearTheBoard()
    }

    private fun getElementSize(): Int {
        // Get the current display size (width and height)
        // Scale the height a bit so it looks better
        val height = (Resources.getSystem().displayMetrics.heightPixels * 0.93).toInt()
        val width = Resources.getSystem().displayMetrics.widthPixels

        // Get the smaller one and divide it by the number of elements for the size of each element
        return (minOf(height, width) / boardObj.boardSize)
    }

    private fun clearTheBoard() {
        boardObj.numberOfPlaced = 0
        boardObj.boardElementsArray.forEach { row ->
            row.forEach {
                it.player = null
                findViewById<ImageButton>(it.viewId).setImageResource(R.drawable.nothing)
            }
        }
    }

    private fun goToNextPlayer() {
        // Add one and then module with the size of the array so we don't go over the index
        currentPlayerArrayIndex = ((currentPlayerArrayIndex + 1) % playerArray.size)
    }
}