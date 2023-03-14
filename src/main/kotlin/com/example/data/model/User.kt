package com.example.data.model

import com.example.data.core.generateUniqueId

data class User(
    val id: Long = generateUniqueId(),
    var userName: String,
    var userPassword: String,
    var salt: String,
    var login: String,
    val listIdFriend: List<Long>,
)
