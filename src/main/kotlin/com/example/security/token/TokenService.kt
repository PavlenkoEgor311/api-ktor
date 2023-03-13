package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*


class TokenService : ITokenService {
    override fun generate(config: TokenConfig, vararg claim: TokenClaim): String {
        val token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        claim.forEach { claim ->
            token.withClaim(claim.name,claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }
}