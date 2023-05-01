package com.example.data.model.user.request

import kotlinx.serialization.Serializable
@Serializable
data class UpdateUserFriendsRequest(
    val idUser: Long,
    val idFriend: Long,
)
