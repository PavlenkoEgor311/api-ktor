package com.example.data.model.user.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val id: Long,
    val username: String?,
    val password: String?,
)

@Serializable
data class FindUserRequest(
    val userId: Long,
    val username: String,
)

@Serializable
data class UserFriendId(
    val userId: Long,
)
