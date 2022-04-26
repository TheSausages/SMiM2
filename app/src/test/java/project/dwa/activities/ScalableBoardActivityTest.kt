package project.dwa.activities

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import project.dwa.models.Board
import project.dwa.models.Player
import project.dwa.models.ViewPlayerConnector
import java.util.stream.Stream

class ScalableBoardActivityTest {
    companion object {
        private const val BOARD_SIZE: Int = 10
        private const val ELEMENTS_TO_WIN: Int = 5

        @JvmStatic
        fun providePlacedWinningPoints(): Stream<Arguments> {
            return Stream.of(
                // Vertical line, but not on column 0
                Arguments.of(
                    listOf(
                        1 to 1,
                        1 to 2,
                        1 to 3,
                        1 to 4,
                        1 to 5
                    )
                ),

                // Vertical line, but on very bottom
                Arguments.of(
                    listOf(
                        9 to 1,
                        9 to 2,
                        9 to 3,
                        9 to 4,
                        9 to 5
                    )
                ),

                // Horizontal line, also not in column zero
                Arguments.of(
                    listOf(
                        4 to 1,
                        5 to 1,
                        6 to 1,
                        7 to 1,
                        8 to 1,
                        9 to 1
                    )
                ),

                // Horizontal line, but opn very left
                Arguments.of(
                    listOf(
                        4 to 9,
                        5 to 9,
                        6 to 9,
                        7 to 9,
                        8 to 9,
                        9 to 9
                    )
                ),

                // Diagonal line, but not using (0, 0)
                Arguments.of(
                    listOf(
                        3 to 3,
                        4 to 4,
                        5 to 5,
                        6 to 6,
                        7 to 7
                    )
                ),

                // Diagonal line, but from bottom
                Arguments.of(
                    listOf(
                        5 to 5,
                        6 to 6,
                        7 to 7,
                        8 to 8,
                        9 to 9
                    )
                ),

                // Anti Diagonal line, but not using (0, 0)
                Arguments.of(
                    listOf(
                        9 to 4,
                        8 to 5,
                        7 to 6,
                        6 to 7,
                        5 to 8
                    )
                )
            )
        }

        @JvmStatic
        fun providePlacedNonWinningPoints(): Stream<Arguments> {
            return Stream.of(
                // Random
                Arguments.of(
                    listOf(
                        0 to 0,
                        5 to 4,
                        9 to 9,
                        3 to 6,
                        8 to 1
                    )
                ),

                // Random
                Arguments.of(
                    listOf(
                        0 to 0,
                        9 to 9,
                        7 to 9,
                        1 to 6,
                        8 to 2,
                        5 to 4,
                        6 to 4,
                        8 to 4
                    )
                ),

                // Random
                Arguments.of(
                    listOf(
                        0 to 7,
                        9 to 1,
                    )
                ),

                // Random
                Arguments.of(
                    listOf(
                        0 to 7,
                        9 to 1,
                        5 to 5,
                        7 to 2,
                        9 to 3
                    )
                ),

                // Horizontal Line with single pause
                Arguments.of(
                    listOf(
                        1 to 1,
                        1 to 2,
                        1 to 3,
                        1 to 4,
                        1 to 6,
                        1 to 7,
                        1 to 8
                    )
                ),

                // Vertical Line with single pause
                Arguments.of(
                    listOf(
                        1 to 1,
                        2 to 1,
                        3 to 1,
                        4 to 1,
                        6 to 1,
                        7 to 1,
                        8 to 1
                    )
                ),

                // Diagonal line with single pause
                Arguments.of(
                    listOf(
                        0 to 0,
                        1 to 1,
                        2 to 2,
                        3 to 3,
                        5 to 5,
                        6 to 6,
                        7 to 7
                    )
                ),

                // Anti diagonal line with single pause
                Arguments.of(
                    listOf(
                        7 to 7,
                        6 to 6,
                        5 to 5,
                        3 to 3,
                        2 to 2,
                        1 to 1,
                        0 to 0
                    )
                )
            )
        }
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

    @ParameterizedTest
    @MethodSource("providePlacedNonWinningPoints")
    fun `when many placed (but not 5 in line) return false`(points: List<Pair<Int, Int>>) {
        //given
        for (pair in points) {
            board.boardElementsArray[pair.first][pair.second] = ViewPlayerConnector(-1, player1)
        }

        val lastPLaced: Pair<Int, Int> = points.last()

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, lastPLaced.first, lastPLaced.second)
        val conditionPlayer2 = board.checkWinCondition(player2, lastPLaced.first, lastPLaced.second)

        //then
        assertFalse(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }

    @ParameterizedTest
    @MethodSource("providePlacedWinningPoints")
    fun `when many placed (winning pattern, but not the same player) return false`(points: List<Pair<Int, Int>>) {
        //given
        val playerList = listOf(player1, player2)
        points.forEachIndexed { index, pair ->
            // Players takes turns placing
            board.boardElementsArray[pair.first][pair.second] = ViewPlayerConnector(-1, playerList[index % playerList.size])
        }

        val lastPLaced: Pair<Int, Int> = points.last()

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, lastPLaced.first, lastPLaced.second)
        val conditionPlayer2 = board.checkWinCondition(player2, lastPLaced.first, lastPLaced.second)

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

    @ParameterizedTest
    @MethodSource("providePlacedWinningPoints")
    fun `when many placed (some are winning) return true`(points: List<Pair<Int, Int>>) {
        //given
        for (pair in points) {
            board.boardElementsArray[pair.first][pair.second] = ViewPlayerConnector(-1, player1)
        }

        val lastPLaced: Pair<Int, Int> = points.last()

        //when
        val conditionPlayer1 = board.checkWinCondition(player1, lastPLaced.first, lastPLaced.second)
        val conditionPlayer2 = board.checkWinCondition(player2, lastPLaced.first, lastPLaced.second)

        //then
        assertTrue(conditionPlayer1)
        assertFalse(conditionPlayer2)
    }
}