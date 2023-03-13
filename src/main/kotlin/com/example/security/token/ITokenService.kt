package com.example.security.token

interface ITokenService {
    fun generate(config: TokenConfig, vararg claim: TokenClaim): String
}