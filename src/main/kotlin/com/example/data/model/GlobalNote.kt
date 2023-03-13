package com.example.data.model

import com.example.data.core.generateUniqueId
import kotlinx.serialization.Serializable


@Serializable
data class GlobalNote(
    val id: Long = generateUniqueId(),
    val idParent: Long,
    val title: String,
    val description: String,
    val date: String,
    val status: String,
    val friendsId: List<Long>,
    val files: List<Int>?,
)
