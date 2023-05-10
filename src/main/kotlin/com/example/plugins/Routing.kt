package com.example.plugins

import com.example.data.model.note.NoteDataSource
import com.example.data.model.notification.NotificationDataSource
import com.example.data.model.user.UserDataSource
import com.example.routes.*
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    noteDataSource: NoteDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    notificationDataSource: NotificationDataSource,
) {
    routing {
        singup(
            hashingService = hashingService,
            userDataSource = userDataSource,
        )
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        getSecretInfo()
        getAllUsers(userDataSource)
        getUserByID(userDataSource)
        getAllNotes(noteDataSource)
        insertNote(noteDataSource, notificationDataSource)
        updateUser(userDataSource, hashingService)
        getNotesUser(noteDataSource)
        updateNote(noteDataSource, notificationDataSource)
        test()
        updateListFriend(userDataSource, notificationDataSource)
        default()
        findFriend(userDataSource)
    }
}