package com.example.data.core

import java.util.*

fun generateUniqueId(): Long {
    val uuid = UUID.randomUUID()
    return uuid.mostSignificantBits xor uuid.leastSignificantBits
}