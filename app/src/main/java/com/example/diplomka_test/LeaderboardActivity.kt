package com.example.diplomka_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.FileNotFoundException
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Spinner


class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var leaderboardAdapter: RecyclerView.Adapter<*>
    private lateinit var sortBySpinner: Spinner
    private var currentMatrixSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Inicializace spinneru pro filtraci podle výsledku
        sortBySpinner = findViewById(R.id.sortBySpinner)

        // Nastavení posluchače pro spinner
        setupSpinner()

        val easyButton: Button = findViewById(R.id.easyButton)
        val mediumButton: Button = findViewById(R.id.mediumButton)
        val hardButton: Button = findViewById(R.id.hardButton)
        val clearButton: Button = findViewById(R.id.clearButton)

        // Tlačítka pro filtraci leaderboardu podle obtížnosti
        easyButton.setOnClickListener {
            currentMatrixSize = 3
            loadUsersByMatrixSize(3)
        }

        mediumButton.setOnClickListener {
            currentMatrixSize = 10
            loadUsersByMatrixSize(10)
        }

        hardButton.setOnClickListener {
            currentMatrixSize = 20
            loadUsersByMatrixSize(20)
        }

        // Tlačítko pro vymazání výsledků
        clearButton.setOnClickListener {
            clearResults()
        }

        // Bez vybrání obtížnosti zobrazí všechny
        loadAllUsers()
    }


    / Fce pro zobrazení všech výsledků
    private fun loadAllUsers() {
        val users = loadUsersFromStorage()
        leaderboardAdapter = LeaderboardAdapter(users)
        recyclerView.adapter = leaderboardAdapter
    }

    // Fce pro filtraci výsledků podle velikosti matice (obtížnosti)
    private fun loadUsersByMatrixSize(matrixSize: Int) {
        val users = loadUsersFromStorage().filter { it.matrixSize == matrixSize }
        leaderboardAdapter = LeaderboardAdapter(users)
        recyclerView.adapter = leaderboardAdapter
    }

    // Fce pro načtení výsledků z .txt
    private fun loadUsersFromStorage(): List<User> {
        val users = mutableListOf<User>()
        try {
            val fileName = "user_results.txt"
            applicationContext.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split(",")
                    if (parts.size == 5) {
                        val userName = parts[0]
                        val matrixSize = parts[1].toInt()
                        val totalAttempts = parts[2].toInt()
                        val successfulAttempts = parts[3].toInt()
                        val averageSpeed = parts[4].toLong()
                        users.add(User(userName, matrixSize, totalAttempts, successfulAttempts, averageSpeed))
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            // Soubor neexistuje nebo je prázdný
        }
        return users
    }

    // Fce pro vymazání obsahu .txt s výsledky
    private fun clearResults() {
        val fileName = "user_results.txt"
        try {
            applicationContext.deleteFile(fileName)
            Toast.makeText(this, "Výsledky byly úspěšně smazány.", Toast.LENGTH_SHORT).show()
            loadAllUsers()
        } catch (e: Exception) {
            Toast.makeText(this, "Nastala chyba při mazání výsledků.", Toast.LENGTH_SHORT).show()
        }
    }


    class LeaderboardAdapter(private val users: List<User>) :
        RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
            return LeaderboardViewHolder(view)
        }

        override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
            val user = users[position]
            holder.bind(user)
        }

        override fun getItemCount(): Int {
            return users.size
        }

        inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(user: User) {
                itemView.findViewById<TextView>(R.id.userNameTextView).text = user.name
                itemView.findViewById<TextView>(R.id.attemptsTextView).text = "Počet pokusů: ${user.totalAttempts}"
                itemView.findViewById<TextView>(R.id.successfulAttemptsTextView).text = "Úspěšné pokusy: ${user.successfulAttempts}"
                itemView.findViewById<TextView>(R.id.averageSpeedTextView).text = "Průměrný čas: ${user.averageSpeed} ms"
            }
        }

    }

    // Enum třída pro možné hodnoty řazení
    enum class SortByOption {
        USER_NAME,
        TOTAL_ATTEMPTS,
        SUCCESSFUL_ATTEMPTS,
        AVERAGE_SPEED
    }

    // Globální proměnná pro uchování aktuálně vybrané možnosti řazení
    private var currentSortOption = SortByOption.USER_NAME

    // V metodě setupSpinner() nastavení posluchače pro změnu výběru ve spinneru
    private fun setupSpinner() {
        val sortByOptions = arrayOf("Jméno", "Počet Pokusů", "Úspěšné pokusy", "Průměrný čas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortByOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.adapter = adapter

        // Nastavení posluchače pro změnu výběru ve spinneru
        sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val sortByOption = when (position) {
                    0 -> SortByOption.USER_NAME
                    1 -> SortByOption.TOTAL_ATTEMPTS
                    2 -> SortByOption.SUCCESSFUL_ATTEMPTS
                    3 -> SortByOption.AVERAGE_SPEED
                    else -> SortByOption.USER_NAME
                }
                currentSortOption = sortByOption
                // Řazení žebříčku podle vybrané možnosti
                sortLeaderboard()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nepotřebujeme implementovat
            }
        }
    }

    // Metoda pro řazení seznamu uživatelů podle aktuálního výběru ve spinneru
    private fun sortLeaderboard() {
        val users = if (currentMatrixSize != 0) {
            loadUsersFromStorage().filter { it.matrixSize == currentMatrixSize }.toMutableList()
        } else {
            loadUsersFromStorage().toMutableList()
        }

        users.sortWith(compareBy<User> {
            when (currentSortOption) {
                SortByOption.USER_NAME -> it.name
                SortByOption.TOTAL_ATTEMPTS -> -it.totalAttempts
                SortByOption.SUCCESSFUL_ATTEMPTS -> -it.successfulAttempts
                SortByOption.AVERAGE_SPEED -> it.averageSpeed
            }
        }.thenBy {
            when (currentSortOption) {
                SortByOption.USER_NAME -> ""
                SortByOption.TOTAL_ATTEMPTS -> 0
                SortByOption.SUCCESSFUL_ATTEMPTS -> 0
                SortByOption.AVERAGE_SPEED -> -it.averageSpeed
            }
        })

        leaderboardAdapter = LeaderboardAdapter(users)
        recyclerView.adapter = leaderboardAdapter
    }
}
