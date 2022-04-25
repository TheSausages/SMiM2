package project.dwa.activities

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import project.dwa.models.Board
import project.dwa.models.Player
import project.dwa.models.ViewPlayerConnector

@ExtendWith(MockKExtension::class)
private const val BOARD_SIZE: Int = 10
class ScalableBoardActivityTest {
    companion object {
        private const val BOARD_SIZE: Int = 10
        private const val ELEMENTS_TO_WIN: Int = 5
    }

    val player1 = Player.createNormalPlayerWithNoSymbol("P1")
    val player2 = Player.createNormalPlayerWithNoSymbol("P2")

    var board: Board = Board(BOARD_SIZE, ELEMENTS_TO_WIN)

    @BeforeEach
    fun `prepare empty Board`() {
        board = Board(BOARD_SIZE, ELEMENTS_TO_WIN)
    }

    @Test
    fun `when all empty return false`() {
        //given

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, 0, 0)
        val conditionPlayer2 = board.checkWinCondition(player2, 0, 0)

        //then
        assertFalse(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }

    @Test
    fun `when 5 in row, return true`() {
        //given
        for (row in 0 until 5) {
            board.boardElementsArray[row][0] = ViewPlayerConnector(-1, player1)
        }

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, 0, 0)
        val conditionPlayer2 = board.checkWinCondition(player2, 0, 0)

        //then
        assertTrue(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }

    @Test
    fun `when 5 in column, return true`() {
        //given
        for (column in 0 until 5) {
            board.boardElementsArray[0][column] = ViewPlayerConnector(-1, player1)
        }

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, 0, 0)
        val conditionPlayer2 = board.checkWinCondition(player2, 0, 0)

        //then
        assertTrue(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }

    @Test
    fun `when 5 in diagonal, return true`() {
        //given
        for (diag in 0 until 5) {
            board.boardElementsArray[diag][diag] = ViewPlayerConnector(-1, player1)
        }

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, 0, 0)
        val conditionPlayer2 = board.checkWinCondition(player2, 0, 0)

        //then
        assertTrue(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }

    @Test
    fun `when 5 in antidiagonal, return true`() {
        //given
        board.boardElementsArray[0][9] = ViewPlayerConnector(-1, player1)
        board.boardElementsArray[1][8] = ViewPlayerConnector(-1, player1)
        board.boardElementsArray[2][7] = ViewPlayerConnector(-1, player1)
        board.boardElementsArray[3][6] = ViewPlayerConnector(-1, player1)
        board.boardElementsArray[4][5] = ViewPlayerConnector(-1, player1)

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, 4, 5)
        val conditionPlayer2 = board.checkWinCondition(player2, 4, 5)

        //then
        assertTrue(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }
}