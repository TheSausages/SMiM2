package project.dwa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import project.dwa.activities.MainMenuActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainMenuActivity = Intent(this, MainMenuActivity::class.java)
        mainMenuActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(mainMenuActivity)
    }
}