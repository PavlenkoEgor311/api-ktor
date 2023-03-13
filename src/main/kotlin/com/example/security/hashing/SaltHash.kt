package com.example.security.hashing

data class SaltHash(
    val hash: String,
    val salt: String,
)