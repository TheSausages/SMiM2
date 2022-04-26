package project.dwa.models

import project.dwa.configuration.BoardFactory

class Board(
    val boardSize: Int,
    val elementsToWin: Int,
    var numberOfPlaced: Int = 0
) {
    var boardElementsArray: Array<Array<ViewPlayerConnector>>

    init {
        boardElementsArray = BoardFactory.createEmptyBoard(boardSize)
    }

    fun checkIfDraw(): Boolean {
        return (boardSize * boardSize) <= numberOfPlaced
    }

    fun checkWinCondition(player: Player, rowIndex: Int, columnIndex: Int): Boolean {
        // Create the necessary variables
        // If the value is negative, give 0, if over the last index make it the last index
        val rowStartIndex = if (rowIndex - elementsToWin < 0) 0 else rowIndex - elementsToWin
        val rowEndIndex = if (rowIndex + elementsToWin > boardSize - 1) boardSize - 1 else rowIndex + elementsToWin
        val columnStartingIndex = if (columnIndex - elementsToWin < 0) 0 else columnIndex - elementsToWin
        val columnEndIndex = if (columnIndex + elementsToWin > boardSize - 1) boardSize - 1 else columnIndex + elementsToWin

        // Search in row
        var numInRow = 0
        for (rowElementIndex in rowStartIndex until rowEndIndex) {
            // Check if the symbol also belong to the player
            if (player.equals(boardElementsArray[rowElementIndex][columnIndex].player)) {
                numInRow++
            } else {
                numInRow = 0
            }

            if (numInRow >= elementsToWin) {
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

            if (numInColumn >= elementsToWin) {
                return true
            }
        }


        // Search in diagonal
        val diagonalRowStartIndex = if (rowIndex - elementsToWin < 0) 0 else rowIndex - elementsToWin
        val diagonalRowEndIndex = if (rowIndex + elementsToWin > boardSize - 1) boardSize - 1 else rowIndex + elementsToWin
        val diagonalColumnStartingIndex = if (columnIndex - elementsToWin < 0) 0 else columnIndex - elementsToWin
        val diagonalColumnEndIndex = if (columnIndex + elementsToWin > boardSize - 1) boardSize - 1 else columnIndex + elementsToWin

        var numInDiagonal = 0
        var diagonalRowIndex = diagonalRowStartIndex
        var diagonalColumnIndex = diagonalColumnStartingIndex
        do {
            if (player.equals(boardElementsArray[diagonalRowIndex][diagonalColumnIndex].player)) {
                numInDiagonal++
            } else {
                numInDiagonal = 0
            }

            if (numInDiagonal >= elementsToWin) {
                return true
            }

            diagonalRowIndex++
            diagonalColumnIndex++
        } while (diagonalRowIndex <= diagonalRowEndIndex && diagonalColumnIndex <= diagonalColumnEndIndex)


        // Search in anti-diagonal
        val antiDiagonalRowStartIndex = rowAntiDiag(rowIndex, columnIndex)
        val antiDiagonalRowEndIndex = if (rowIndex + elementsToWin > boardSize - 1) boardSize - 1 else rowIndex + elementsToWin
        val antiDiagonalColumnStartingIndex = if (columnIndex - elementsToWin < 0) 0 else columnIndex - elementsToWin
        val antiDiagonalColumnEndIndex = if (columnIndex + elementsToWin > boardSize - 1) boardSize - 1 else columnIndex + elementsToWin

        var numInAntiDiagonal = 0
        var antiDiagonalRowIndex = antiDiagonalRowStartIndex
        var antiDiagonalColumnIndex = antiDiagonalColumnEndIndex
        do {
            if (player.equals(boardElementsArray[antiDiagonalRowIndex][antiDiagonalColumnIndex].player)) {
                numInAntiDiagonal++
            } else {
                numInAntiDiagonal = 0
            }

            if (numInAntiDiagonal >= elementsToWin) {
                return true
            }

            antiDiagonalRowIndex++
            antiDiagonalColumnIndex--
        } while (antiDiagonalRowIndex <= antiDiagonalRowEndIndex && antiDiagonalColumnIndex >= antiDiagonalColumnStartingIndex)

        return false
    }

    private fun rowAntiDiag(rowNum: Int, colNum: Int): Int {
        var colNumVar = colNum

        for (row in rowNum downTo 0) {
            println("$row $colNumVar")
            if (colNumVar >= (boardSize - 1) || colNumVar <= 0  || row >= (boardSize - 1) || row <= 0) {
                return row
            }

            colNumVar++
        }

        return rowNum
    }
}