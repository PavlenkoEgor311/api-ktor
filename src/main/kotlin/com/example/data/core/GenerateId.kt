package com.example.data.core

import java.util.*

fun generateUniqueId(): Long {
    val uuid = UUID.randomUUID()
    val id = uuid.mostSignificantBits xor uuid.leastSignificantBits
    return if (id < 0) id * (-1) else id
}