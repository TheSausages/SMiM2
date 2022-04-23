package project.dwa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import project.dwa.activities.ScalableBoardActivity
import project.dwa.models.Player

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val boardIntent = Intent(this, ScalableBoardActivity::class.java)

        boardIntent.putExtra("size", 10)
        boardIntent.putExtra("elementsToWin", 4)

        boardIntent.putParcelableArrayListExtra("players", arrayListOf(
            Player("Player 1", R.drawable.x, false),
            Player("Player 2", R.drawable.o),
            Player("Machine", R.drawable.gear, true)
        ))
        boardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(boardIntent)
    }
}