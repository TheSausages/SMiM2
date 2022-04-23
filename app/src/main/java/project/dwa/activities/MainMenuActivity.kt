package project.dwa.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import project.dwa.R
import project.dwa.models.Player

class MainMenuActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val size = 10
        val elementsToWin = 4

        // Use the menu layout
        setContentView(R.layout.main_menu)

        // Set the click listener for the 'Play with Machine' Button
        val playWithMachineButton: Button = findViewById(R.id.play_machine_button_id)
        playWithMachineButton.setOnClickListener {
            val boardIntent = Intent(this, ScalableBoardActivity::class.java)

            boardIntent.putExtra("size", size)
            boardIntent.putExtra("elementsToWin", elementsToWin)

            boardIntent.putParcelableArrayListExtra("players", arrayListOf(
                Player.createNormalPlayer("Player 1", R.drawable.x),
                Player.createMachinePlayer()
            ))

            startActivity(boardIntent)
        }

        // Set the click listener for the 'Play with Another' Button
        val playWithAnotherButton: Button = findViewById(R.id.play_another_button_id)
        playWithAnotherButton.setOnClickListener {
            val boardIntent = Intent(this, ScalableBoardActivity::class.java)

            boardIntent.putExtra("size", size)
            boardIntent.putExtra("elementsToWin", elementsToWin)

            boardIntent.putParcelableArrayListExtra("players", arrayListOf(
                Player.createNormalPlayer("Player 1", R.drawable.x),
                Player.createNormalPlayer("Player 2", R.drawable.o)
            ))

            startActivity(boardIntent)
        }
    }
}