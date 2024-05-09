package com.example.diplomka_test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val startButtonEasy: Button = findViewById(R.id.startButtonEasy)
        startButtonEasy.setOnClickListener {
            startGame(3)
        }

        val startButtonMedium: Button = findViewById(R.id.startButtonMedium)
        startButtonMedium.setOnClickListener {
            startGame(10)
        }

        val startButtonHard: Button = findViewById(R.id.startButtonHard)
        startButtonHard.setOnClickListener {
            startGame(20)
        }
        val showLeaderboardButton: Button = findViewById(R.id.showLeaderboardButton)
        showLeaderboardButton.setOnClickListener {
            openLeaderboard()
        }



    }

    private fun startGame(matrixSize: Int) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("MATRIX_SIZE", matrixSize)

        val nameEditText: EditText = findViewById(R.id.userNameEditText)
        val userName = nameEditText.text.toString()
        // Předej jméno uživatele do GameActivity
        intent.putExtra("USER_NAME", userName)

        // Získání počtu pokusů z EditText
        val pokusyEditText: EditText = findViewById(R.id.pokusyEditText)
        val početPokusu = pokusyEditText.text.toString().toIntOrNull() ?: GameActivity.DEFAULT_MAX_ATTEMPTS
        intent.putExtra("MAX_ATTEMPTS", početPokusu)

        startActivity(intent)
    }

    private fun openLeaderboard() {
        val intent = Intent(this, LeaderboardActivity::class.java)
        startActivity(intent)
    }
}