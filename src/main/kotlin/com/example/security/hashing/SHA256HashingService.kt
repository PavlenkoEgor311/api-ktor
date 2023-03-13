package com.example.security.hashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.net.SecureCacheResponse
import java.security.SecureRandom
import java.util.HexFormat

class SHA256HashingService : HashingService {
    override fun generateSaltHash(value: String, saltLength: Int): SaltHash {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        val saltAsHex = Hex.encodeHexString(salt)
        val hash = DigestUtils.sha256Hex("$saltAsHex$value")
        return SaltHash(hash, saltAsHex)
    }

    override fun verify(value: String, salt: SaltHash): Boolean =
        DigestUtils.sha256Hex(salt.salt + value) == salt.hash
}