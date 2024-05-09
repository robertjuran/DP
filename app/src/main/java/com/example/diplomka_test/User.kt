package com.example.diplomka_test

data class User(
    val name: String,
    val matrixSize: Int,
    var totalAttempts: Int,
    var successfulAttempts: Int,
    val averageSpeed: Long
)
