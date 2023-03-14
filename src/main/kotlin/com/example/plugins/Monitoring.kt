package com.example.plugins

import io.ktor.http.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*
import io.ktor.server.request.*
import io.ktor.server.application.*

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val message = "Обработка запроса: ${call.request.uri} - ${call.response.status()}"
            if (call.response.status() == HttpStatusCode.OK) message else "Ошибка: $message"
        }
    }
}
