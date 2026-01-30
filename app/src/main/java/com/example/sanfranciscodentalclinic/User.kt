package com.example.sanfranciscodentalclinic

data class User(
    val uid: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
