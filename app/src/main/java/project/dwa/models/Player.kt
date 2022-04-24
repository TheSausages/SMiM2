package project.dwa.models

import android.os.Parcel
import android.os.Parcelable
import project.dwa.R

// Player object, not a machine by default
class Player(
    val name: String,
    val symbol: Int,
    val isMachine: Boolean = false,
    var winsCounter: Int = 0
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (name != other.name) return false
        if (symbol != other.symbol) return false
        if (isMachine != other.isMachine) return false
        if (winsCounter != other.winsCounter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + symbol
        result = 31 * result + isMachine.hashCode()
        result = 31 * result + winsCounter
        return result
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(symbol)
        parcel.writeByte(if (isMachine) 1 else 0)
        parcel.writeInt(winsCounter)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }

        fun createMachinePlayer(): Player {
            return Player("Machine", R.drawable.gear, true, 0)
        }

        fun createNormalPlayer(name: String, symbol: Int): Player {
            return Player(name, symbol, false, 0)
        }

        fun createNormalPlayerWithNoSymbol(name: String): Player {
            return Player(name, R.drawable.nothing, false, 0)
        }
    }
}