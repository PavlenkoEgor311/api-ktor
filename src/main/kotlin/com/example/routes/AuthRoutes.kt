package com.example.routes

import com.example.data.core.generateUniqueId
import com.example.data.model.Notification
import com.example.data.model.User
import com.example.data.model.notification.NotificationDataSource
import com.example.data.model.user.UserDataSource
import com.example.data.model.user.request.*
import com.example.data.model.user.response.AuthResponse
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun Route.singup(
    hashingService: HashingService,
    userDataSource: UserDataSource,
) {
    post("signup") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val areFieldsBlank = request.userName.isBlank() || request.password.isBlank()
        val passShort = request.password.length < 5
        if (userDataSource.getUserName(request.userName) != null && userDataSource.getUserLogin(request.login) != null) {
            call.respond(HttpStatusCode.Conflict, "Username or Login is already exists")
            return@post
        }
        if (areFieldsBlank || passShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val saltHash = hashingService.generateSaltHash(request.password)
        val user = User(
            userName = request.userName,
            userPassword = saltHash.hash,
            salt = saltHash.salt,
            login = request.login,
            listIdFriend = mutableListOf(),
            listIdNote = mutableListOf(),
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        } else {
            call.respond(HttpStatusCode.OK)
            return@post
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            return@get call.respond(HttpStatusCode.OK, "Your id: $userId")
        }
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveOrNull<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = userDataSource.getUserLogin(request.login)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            salt = SaltHash(
                hash = user.userPassword,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            LoginResponse(
                token = token,
                idUser = user.id,
            )
        )
    }
}

fun Route.getAllUsers(
    userDataSource: UserDataSource
) {
    get("usersAll") {
        val users = withContext(Dispatchers.IO) {
            userDataSource.getAllUsers()
        }
        val json =
            buildJsonArray {
                users.forEach {
                    add(buildJsonObject {
                        put("id", it.id)
                        put("username", it.userName)
                        put("userpassword", it.userPassword)
                        put("friendsID", buildJsonArray {
                            it.listIdFriend.forEach { id ->
                                add(buildJsonObject {
                                    put("id", id)
                                })
                            }
                        })
                    })
                }
            }
        call.respond(HttpStatusCode.OK, json)
    }
}

fun Route.getUserByID(
    userDataSource: UserDataSource,
) {
    get("getUserById") {
        val id = call.request.queryParameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            return@get
        }

        val user = userDataSource.getUserById(id)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User not found")
        } else {
            call.respond(HttpStatusCode.OK, buildJsonObject {
                put("id", user.id)
                put("username", user.userName)
                put("login", user.login)
                put("friendsId", buildJsonArray {
                    user.listIdFriend.forEach {
                        add(buildJsonObject {
                            put("id", it)
                        })
                    }
                })
            })
        }
    }
}

fun Route.updateUser(
    userDataSource: UserDataSource,
    hashingService: HashingService
) {
    post("updateUser") {
        val request = call.receiveOrNull<UpdateUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Not valid")
            return@post
        }
        if (request.login.isNullOrEmpty() ||
            request.username.isNullOrEmpty() ||
            request.password.isNullOrEmpty()
        )
            return@post call.respond(HttpStatusCode.BadRequest, "Not valid user data")

        val respond = userDataSource.updateUserData(request, hashingService)
        if (respond.matchedCount > 0)
            call.respond(HttpStatusCode.OK, "Success update user")
        else
            call.respond(HttpStatusCode.BadRequest, "Not valid")
    }
}

fun Route.updateListFriend(userDataSource: UserDataSource, notificationDataSource: NotificationDataSource) {
    patch("addFriend") {
        try {
            val request = call.receive<UpdateUserFriendsRequest>()
            val respond = userDataSource.addFriendUser(request.idUser, request.idFriend)
            if (respond.matchedCount > 0) {
                notificationDataSource.insertNotification(
                    Notification(
                        id = generateUniqueId(),
                        userId = request.idFriend,
                        type = 2,
                        body = "Обновление списка друзей",
                        isShow = false,
                    )
                )
                call.respond(HttpStatusCode.OK, "Пользователь успешно добавлен")
            } else
                call.respond(HttpStatusCode.Conflict, "Пользователь не добавлен. Попробуйте еще раз")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Ошибка при десериализации тела запроса: ${e.message}")
        }
    }
    delete("delFriend") {
        try {
            val request = call.receive<UpdateUserFriendsRequest>()
            withContext(Dispatchers.IO) {
                val respond = userDataSource.delFriendUser(request.idUser, request.idFriend)
                if (respond.matchedCount > 0) {
                    notificationDataSource.insertNotification(
                        Notification(
                            id = generateUniqueId(),
                            userId = request.idFriend,
                            type = 2,
                            body = "Обновление списка друзей",
                            isShow = false,
                        )
                    )
                    call.respond(HttpStatusCode.OK, "Пользователь успешно удален")
                } else
                    call.respond(HttpStatusCode.Conflict, "Пользователь не удален. Попробуйте еще раз")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Ошибка при десериализации тела запроса: ${e.message}")
        }
    }
}