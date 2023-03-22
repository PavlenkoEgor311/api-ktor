package com.example.routes

import com.example.data.model.User
import com.example.data.model.user.UserDataSource
import com.example.data.model.user.request.AuthRequest
import com.example.data.model.user.request.UpdateUserRequest
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
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val passShort = request.password.length < 8
        if (userDataSource.getUserName(request.username) != null) {
            call.respond(HttpStatusCode.Conflict, "Username is already exists pizda")
            return@post
        }
        if (areFieldsBlank || passShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val saltHash = hashingService.generateSaltHash(request.password)
        val user = User(
            username = request.username,
            userPassword = saltHash.hash,
            salt = saltHash.salt,
            login = request.login,
            listIdFriend = listOf(3481136338176530394),
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        } else {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your id: $userId")
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
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val user = userDataSource.getUserName(request.username)
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
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.getAllUsers(
    userDataSource: UserDataSource
) {
    get("usersAll") {
        val users = userDataSource.getAllUsers()
        val json =
            buildJsonArray {
                users.forEach {
                    add(buildJsonObject {
                        put("id", it.id)
                        put("username", it.username)
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
                put("username", user.username)
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
)  {
    post("updateuser") {
        val request = call.receiveOrNull<UpdateUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Упс")
            return@post
        }
        if (request.login.isNullOrEmpty() ||
            request.username.isNullOrEmpty() ||
            request.password.isNullOrEmpty()
        ) return@post call.respond(HttpStatusCode.BadRequest, "Not valid user data")

        val respond = userDataSource.updateUserData(request, hashingService)
        if (respond.matchedCount > 0)
            call.respond(HttpStatusCode.OK, "Success update user")
        else
            call.respond(HttpStatusCode.BadRequest, "Not valid")
    }
}