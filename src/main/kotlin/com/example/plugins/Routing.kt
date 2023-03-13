package com.example.plugins

import com.example.data.model.user.UserDataSource
import com.example.routes.*
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    routing {
        singup(
            hashingService = hashingService,
            userDataSource = userDataSource,
        )
        signIn(
            userDataSource,
            hashingService,
            tokenService,
            tokenConfig,
        )
        getSecretInfo()
        getAllUsers(userDataSource)
        getUserByID(userDataSource)
    }
}