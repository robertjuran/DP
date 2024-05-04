package com.example.diplomka_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.FileNotFoundException

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var sortBySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setContentView(R.layout.activity_leaderboard)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sortBySpinner = findViewById(R.id.sortBySpinner) // Initialize sortBySpinner

        val sortByOptions = arrayOf("Jméno", "Počet Pokusů", "Úspěšné pokusy", "Průměrný čas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortByOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.adapter = adapter

        val users = loadUsersFromStorage()

        leaderboardAdapter = LeaderboardAdapter(users)
        recyclerView.adapter = leaderboardAdapter

        leaderboardAdapter.setShowHeader(true) // Zobrazit hlavičku

        sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Sort data based on selected option
                val selectedSortOption = sortByOptions[position]
                leaderboardAdapter.sortBy(selectedSortOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadUsersFromStorage(): List<User> {
        val users = mutableListOf<User>()
        try {
            val fileName = "user_results.txt"
            applicationContext.openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split(",")
                    if (parts.size == 4) {
                        val userName = parts[0]
                        val totalAttempts = parts[1].toInt()
                        val successfulAttempts = parts[2].toInt()
                        val averageSpeed = parts[3].toLong()
                        users.add(User(userName, totalAttempts, successfulAttempts, averageSpeed))
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            // File does not exist or is empty
        }
        return users
    }

    class LeaderboardAdapter(private var users: List<User>) :
        RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

        private var showHeader = true // Zobrazovat hlavičku

        fun setShowHeader(show: Boolean) {
            showHeader = show
            notifyDataSetChanged() // Aktualizovat zobrazení
        }

        fun sortBy(option: String) {
            // Sort data based on the selected option
            when (option) {
                "Jméno" -> users = users.sortedBy { it.name }
                "Počet Pokusů" -> users = users.sortedByDescending { it.totalAttempts }
                "Úspěšné pokusy" -> users = users.sortedByDescending { it.successfulAttempts }
                "Průměrný čas" -> users = users.sortedBy { it.averageSpeed }
            }
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
            val layoutResId = if (viewType == VIEW_TYPE_HEADER) R.layout.item_leaderboard_header else R.layout.item_leaderboard
            val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
            return LeaderboardViewHolder(view)
        }

        override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
            if (getItemViewType(position) == VIEW_TYPE_HEADER) {
                holder.bindHeader() // Nastavit hlavičku
            } else {
                val user = users[position - if (showHeader) 1 else 0]
                holder.bind(user)
            }
        }

        override fun getItemCount(): Int {
            return users.size + if (showHeader) 1 else 0
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == 0 && showHeader) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
        }

        inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(user: User) {
                itemView.findViewById<TextView>(R.id.userNameTextView).text = user.name
                itemView.findViewById<TextView>(R.id.attemptsTextView).text = user.totalAttempts.toString()
                itemView.findViewById<TextView>(R.id.successfulAttemptsTextView).text = user.successfulAttempts.toString()
                itemView.findViewById<TextView>(R.id.averageSpeedTextView).text = "${user.averageSpeed} ms"
            }

            fun bindHeader() {
                itemView.findViewById<TextView>(R.id.userNameTextView).text = "Jméno"
                itemView.findViewById<TextView>(R.id.attemptsTextView).text = "Počet Pokusů"
                itemView.findViewById<TextView>(R.id.successfulAttemptsTextView).text = "Úspěšné pokusy"
                itemView.findViewById<TextView>(R.id.averageSpeedTextView).text = "Průměrný čas"
            }
        }

        companion object {
            private const val VIEW_TYPE_HEADER = 0
            private const val VIEW_TYPE_ITEM = 1
        }
    }
}

