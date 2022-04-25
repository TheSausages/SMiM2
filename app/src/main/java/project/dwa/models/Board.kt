package project.dwa.models

import project.dwa.configuration.BoardFactory

class Board(
    val boardSize: Int,
    val elementsToWin: Int,
    var numberOfPlaced: Int = 0
) {
    val boardElementsArray: Array<Array<ViewPlayerConnector>>

    init {
        boardElementsArray = BoardFactory.createEmptyBoard(boardSize)
    }

    fun checkIfDraw(): Boolean {
        return (boardSize * boardSize) <= numberOfPlaced
    }

    fun checkWinCondition(player: Player, rowIndex: Int, columnIndex: Int): Boolean {
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
                return true
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
                return true
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
                return true
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
                return true
            }

            antiDiagonalRowIndex++
            antiDiagonalColumnIndex--
        } while (antiDiagonalRowIndex < rowEndIndex && antiDiagonalColumnIndex > columnStartingIndex)

        return false
    }
}