package project.dwa.models

import android.os.Parcel
import android.os.Parcelable

data class ViewPlayerConnector(val viewId: Int, var player: Player?): Parcelable {
    constructor() : this(-1, null)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Player::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(viewId)
        parcel.writeParcelable(player, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ViewPlayerConnector> {
        override fun createFromParcel(parcel: Parcel): ViewPlayerConnector {
            return ViewPlayerConnector(parcel)
        }

        override fun newArray(size: Int): Array<ViewPlayerConnector?> {
            return arrayOfNulls(size)
        }
    }
}