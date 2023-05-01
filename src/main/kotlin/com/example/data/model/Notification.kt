package com.example.data.model

data class Notification(
    val id: Long,
    val userId: Long,
    val type: Int,
    val body: String,
    val isShow: Boolean,
)
