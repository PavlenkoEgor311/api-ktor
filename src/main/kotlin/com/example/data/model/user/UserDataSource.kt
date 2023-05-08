package com.example.data.model.user

import com.example.data.model.User
import com.example.data.model.user.request.UpdateUserRequest
import com.example.security.hashing.HashingService
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import io.ktor.server.plugins.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq

class UserDataSource(db: CoroutineDatabase) : IUserDataSource {

    private val users = db.getCollection<User>("user")
    override suspend fun getUserName(name: String): User? = users.findOne(User::userName eq name)
    override suspend fun getUserLogin(login: String): User? = users.findOne(User::login eq login)
    override suspend fun isAuthUser(login: String, password:String): User? = users.findOne(User::login eq login, User::userPassword eq password)
    override suspend fun insertUser(user: User): Boolean = users.insertOne(user).wasAcknowledged()
    override suspend fun getAllUsers() = users.collection.find().toList()
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

    override suspend fun addFriendUser(idUser: Long, idFriend: Long): UpdateResult {
        val currentUser = users.findOne(User::id eq idUser)
        val friend = users.findOne(User::id eq idFriend)
        if (currentUser != null && friend != null) {
            if (currentUser.listIdFriend.contains(idFriend)) {
                throw BadRequestException("Пользователь уже добавлен")
            } else {
                currentUser.listIdFriend.add(idFriend)
                return users.updateOne(
                    filter = User::id eq idUser,
                    target = currentUser,
                    options = UpdateOptions().upsert(false),
                )
            }
        } else throw NotFoundException()
    }

    override suspend fun delFriendUser(idUser: Long, idFriend: Long): UpdateResult {
        val currentUser = users.findOne(User::id eq idUser)
        val friend = users.findOne(User::id eq idFriend)
        if (currentUser != null && friend != null) {
            if (currentUser.listIdFriend.contains(idFriend)) {
                throw BadRequestException("Пользователь уже удален")
            } else {
                currentUser.listIdFriend.remove(idFriend)
                return users.updateOne(
                    filter = User::id eq idUser,
                    target = currentUser,
                    options = UpdateOptions().upsert(false),
                )
            }
        } else throw NotFoundException()
    }
}