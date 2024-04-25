package com.example.diplomka_test

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private companion object {
        const val MATRIX_SIZE = 5
        const val MAX_ATTEMPTS = 10
    }

    private lateinit var buttons: Array<Array<MyButton>>
    private var totalAttempts = 0
    private var successfulAttempts = 0
    private var litButton: MyButton? = null

    // Časové proměnné pro měření času mezi kliknutími
    private var startTime: Long = 0
    private var totalTime: Long = 0
    private var clickCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setContentView(R.layout.activity_game)

        val matrixSize = intent.getIntExtra("MATRIX_SIZE", 3)

        createMatrix(matrixSize)

        val endButton: Button = findViewById(R.id.endButton)
        endButton.setOnClickListener {
            endGame()
        }

        // Inicializace startTime při spuštění aktivity
        startTime = SystemClock.elapsedRealtime()

        lightRandomButton()
    }

    private fun createMatrix(matrixSize: Int) {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        gridLayout.removeAllViews()
        gridLayout.columnCount = matrixSize
        gridLayout.rowCount = matrixSize

        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        val buttonWidth = ((screenWidth - 40) / matrixSize )
        val buttonHeight = ((screenHeight - 40) / matrixSize )

        buttons = Array(matrixSize) { row ->
            Array(matrixSize) { col ->
                MyButton(this, row, col).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = buttonWidth
                        height = buttonHeight
                        rowSpec = GridLayout.spec(row)
                        columnSpec = GridLayout.spec(col)
                    }
                    setOnClickListener {
                        onButtonClick(this)
                    }
                    gridLayout.addView(this)
                }
            }
        }
    }

    private fun lightRandomButton() {
        val random = Random.Default
        val row = random.nextInt(buttons.size)
        val col = random.nextInt(buttons.size)

        litButton = buttons[row][col]
        litButton?.lightUp()
    }

    private fun onButtonClick(button: MyButton) {
        totalAttempts++

        if (button != litButton) {
            if (totalAttempts >= MAX_ATTEMPTS) {
                endGame()
            }
            return
        }

        successfulAttempts++
        button.turnOff()

        // Získání času mezi kliknutími a aktualizace celkového času a počtu kliknutí
        val currentTime = SystemClock.elapsedRealtime()
        totalTime += if (clickCount > 0) currentTime - startTime else 0
        clickCount++
        startTime = currentTime

        if (totalAttempts < MAX_ATTEMPTS) {
            lightRandomButton()
        } else {
            endGame()
        }
    }

    private fun removeMatrix() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        gridLayout.removeAllViews()
    }

    private fun endGame() {
        removeMatrix()
        val accuracy = calculateAccuracy()
        val accuracyTextView: TextView = findViewById(R.id.accuracyTextView)
        accuracyTextView.text = String.format("Přesnost: %.2f %%", accuracy)

        // Výpočet průměrného času kliknutí a zobrazení na konci hry
        val averageTimePerClick = if (clickCount > 0) totalTime.toDouble() / clickCount else 0.0
        val averageTimeTextView: TextView = findViewById(R.id.averageTimeTextView)
        averageTimeTextView.text = String.format("Průměrný čas kliknutí: %.2f ms", averageTimePerClick)

        val endButton: Button = findViewById(R.id.endButton)
        endButton.text = "Zpět na menu"
        endButton.visibility = View.VISIBLE
        endButton.setOnClickListener {
            returnToMenu()
        }
    }

    private fun returnToMenu() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun calculateAccuracy(): Double {
        return if (totalAttempts == 0) {
            0.0
        } else {
            (successfulAttempts.toDouble() / totalAttempts) * 100
        }
    }
}

class MyButton(context: Context, val row: Int, val col: Int) : androidx.appcompat.widget.AppCompatButton(context) {

    init {
        setBackgroundColor(Color.WHITE)
    }

    fun lightUp() {
        setBackgroundColor(Color.RED)
    }

    fun turnOff() {
        setBackgroundColor(Color.WHITE)
    }
}