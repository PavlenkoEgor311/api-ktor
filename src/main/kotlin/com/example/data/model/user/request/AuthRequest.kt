package com.example.data.model.user.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val userName: String,
    val password: String,
    val login: String,
)