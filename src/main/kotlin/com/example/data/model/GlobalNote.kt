package com.example.data.model

import com.example.data.core.generateUniqueId
import kotlinx.serialization.Serializable

@Serializable
data class GlobalNote(
    val id: Long = generateUniqueId(),
    var idParent: Long,
    var title: String,
    var description: String,
    var date: String,
    var status: String,
    var friendsId: List<Long>?,
    var files: List<Int>?,
)
