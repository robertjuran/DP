package com.example.diplomka_test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Skrytí "status baru"
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Skrytí "action baru"
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

// "pošleme" hodnotu matice podle vybrané obtížnosti v menu
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
    }

    private fun startGame(matrixSize: Int) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("MATRIX_SIZE", matrixSize)
        startActivity(intent)
    }
}