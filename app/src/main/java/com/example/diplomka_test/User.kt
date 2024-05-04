package com.example.diplomka_test

data class User(
    val name: String,
    var totalAttempts: Int,
    var successfulAttempts: Int,
    // var totalTime: Long,
    val averageSpeed: Long
)
