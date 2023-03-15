package com.example.routes

import com.example.data.core.isValid
import com.example.data.model.GlobalNote
import com.example.data.model.note.NoteDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun Route.getAllNotes(noteDataSource: NoteDataSource) {
    get("getallnote") {
        val json = buildJsonArray {
            noteDataSource.getAllNotes().forEach { note ->
                add(buildJsonObject {
                    put("id", note.id)
                    put("parentId", note.idParent)
                    put("title", note.title)
                    put("description", note.description)
                    put("date", note.date)
                    put("status", note.status)
                    put("friendsID", buildJsonArray {
                        note.friendsId?.forEach { id ->
                            add(buildJsonObject {
                                put("id", id)
                            })
                        }
                    })
                    put("files", buildJsonArray {
                        note.files?.forEach { files ->
                            add(buildJsonObject {
                                put("id", files)
                            })
                        }
                    })
                })
            }
        }
        call.respond(HttpStatusCode.OK, json)
    }
}

fun Route.insertNote(noteSource: NoteDataSource) {
    post("setnote") {
        val note = call.receiveOrNull<GlobalNote>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Not valid note")
            return@post
        }
        if (!note.isValid()) {
            call.respond(HttpStatusCode.BadRequest, "Not valid note")
            return@post
        }
        val wasAcknowledged = noteSource.insertNote(note)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getNotesUser(noteDataSource: NoteDataSource) {
    get("getNotesUserById") {
        val id = call.request.queryParameters["idUser"]?.toLongOrNull() ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            "Not valid id"
        )

        val notes = noteDataSource.getNotesUser(id)
        call.respond(HttpStatusCode.OK, buildJsonArray {
            notes.forEach { note ->
                add(buildJsonObject {
                    put("id", note.id)
                    put("parentId", note.idParent)
                    put("title", note.title)
                    put("description", note.description)
                    put("date", note.date)
                    put("status", note.status)
                    put("friendsID", buildJsonArray {
                        note.friendsId?.forEach { id ->
                            add(buildJsonObject {
                                put("id", id)
                            })
                        }
                    })
                    put("files", buildJsonArray {
                        note.files?.forEach { files ->
                            add(buildJsonObject {
                                put("id", files)
                            })
                        }
                    })
                })
            }
        })

    }
}

fun Route.updateNote(noteDataSource: NoteDataSource) {
    post("updateNote") {
        val request = call.receiveOrNull<GlobalNote>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (!request.isValid())
            return@post call.respond(HttpStatusCode.BadRequest, "Not valid note")

        val respond = noteDataSource.updateNote(request)
        if (respond.matchedCount > 0)
            call.respond(HttpStatusCode.OK, "Success update note")
        else
            call.respond(HttpStatusCode.BadRequest, "Not valid")
    }
}

fun Route.test() {
    get("test") {
        call.respondText("Hello, дебил!")
    }
}

