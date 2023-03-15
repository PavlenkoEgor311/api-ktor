package com.example

import com.example.data.model.note.NoteDataSource
import com.example.data.model.user.UserDataSource
import io.ktor.server.application.*
import com.example.plugins.*
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    val mongodb = "my_note"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://EgorAdmin:1qwertyY@atlascluster.sxtjrng.mongodb.net/$mongodb?retryWrites=true&w=majority"
    )
        .coroutine
        .getDatabase(mongodb)

    val userDataSource = UserDataSource(db)
    val noteDataSource = NoteDataSource(db)

    val tokenService = TokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").toString(),
        audience = environment.config.property("jwt.audience").toString(),
        expiresIn = 365L * 1000L * 60L * 24L,
        secret = environment.config.property("jwt.secret").toString()
    )
    val hashingService = SHA256HashingService()
    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig,
        noteDataSource = noteDataSource,
    )
}