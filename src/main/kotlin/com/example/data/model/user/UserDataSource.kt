package com.example.data.model.user

import com.example.data.model.User
import com.example.data.model.user.request.UpdateUserRequest
import com.example.security.hashing.HashingService
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import io.ktor.server.plugins.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSource(db: CoroutineDatabase) : IUserDataSource {

    private val users = db.getCollection<User>("user")
    override suspend fun getUserName(name: String): User? = users.findOne(User::userName eq name)
    override suspend fun insertUser(user: User): Boolean = users.insertOne(user).wasAcknowledged()
    override suspend fun getAllUsers() = users.find().toList()
    override suspend fun getUserById(id: Long): User? = users.findOne(User::id eq id)
    override suspend fun updateUserData(user: UpdateUserRequest, hashingService: HashingService): UpdateResult {
        val currentUser = users.findOne(User::id eq user.id)
        val hashing = hashingService.generateSaltHash(user.password!!)
        if (currentUser != null) {
            currentUser.login = user.login!!
            currentUser.userName = user.username!!
            currentUser.userPassword = hashing.hash
            currentUser.salt = hashing.salt
            return users.updateOne(
                filter = User::id eq user.id,
                target = currentUser,
                options = UpdateOptions().upsert(false)
            )
        } else throw NotFoundException()
    }
}