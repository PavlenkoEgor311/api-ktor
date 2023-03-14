package com.example.data.model.user.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val id: Long,
    val username: String?,
    val login: String?,
    val password: String?,
)
