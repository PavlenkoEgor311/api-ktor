package com.example.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.*
import io.ktor.serialization.jackson.*

fun Application.configureSerialization() {
    val objectMapper = ObjectMapper()
    objectMapper.registerModule(KotlinModule())
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
    install(ContentNegotiation) {
        json()
        jacksonObjectMapper()
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
        jackson {
            registerModule(KotlinModule())
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

}
