package com.example.data.model.user.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserFriendsRequest(
    val idFriend: Long
)
