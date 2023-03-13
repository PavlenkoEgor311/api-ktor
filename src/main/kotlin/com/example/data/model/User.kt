package com.example.data.model

import com.example.data.core.generateUniqueId

data class User(
    val id: Long = generateUniqueId(),
    val userName: String,
    val userPassword: String,
    val salt: String,
    val login: String,
    val listIdFriend: List<Long>,
)
