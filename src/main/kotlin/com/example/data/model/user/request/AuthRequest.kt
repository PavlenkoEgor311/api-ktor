package com.example.data.model.user.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val userName: String,
    val login: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val login: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    val idUser: Long,
)