package project.dwa.models

import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class GameHistoryItem(
    val winner: Player,
    val otherPlayers: List<Player>,
    val playDate: Date = Calendar.getInstance().time
) {
    companion object {
        // Use only date, and format it for the Polish locale
        private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale("pl"))

        const val HISTORY_FILE_NAME: String = "history.txt"
        const val GAME_HISTORY_ITEM_DATA_SPLITTER: String = ";"
        val WRITING_CHARSET: Charset = Charset.forName("UTF-8")

        fun fromString(historyItemStr: String): GameHistoryItem {
            val elements = historyItemStr.split(GAME_HISTORY_ITEM_DATA_SPLITTER)

            return GameHistoryItem(
                Player.createNormalPlayerWithNoSymbol(elements[1]),
                ArrayList(elements[2].split(",").map { elem -> Player.createNormalPlayerWithNoSymbol(elem) }),
                dateFormatter.parse(elements[0])!!
            )
        }
    }

    fun getFormattedPLayDate(): String {
        return dateFormatter.format(playDate)
    }

    fun getOtherPlayersString(): String {
        return otherPlayers.joinToString("") { player -> player.name }
    }

    fun toPrettyString(): String {
        return "${dateFormatter.format(playDate)} ${winner.name} ${otherPlayers.joinToString("") { player -> player.name }}"
    }

    override fun toString(): String {
        return "${dateFormatter.format(playDate)}$GAME_HISTORY_ITEM_DATA_SPLITTER${winner.name}$GAME_HISTORY_ITEM_DATA_SPLITTER${otherPlayers.joinToString("") { player -> player.name }}"
    }

    fun toStringWithNewline(): String {
        return toString() + "\n"
    }
}