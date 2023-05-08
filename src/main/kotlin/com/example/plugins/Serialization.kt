package com.example.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
        jacksonObjectMapper()
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

}
