package com.example.data.model.user.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token:String
)