package com.example.security.hashing

import io.netty.handler.codec.FixedLengthFrameDecoder

interface HashingService {
    fun generateSaltHash(value: String, saltLength: Int = 32): SaltHash
    fun verify(value: String, salt: SaltHash): Boolean
}