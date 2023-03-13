package com.example.data.model.user

import com.example.data.model.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSource(db: CoroutineDatabase) : IUserDataSource {

    private val users = db.getCollection<User>()
    override suspend fun getUserName(name: String): User? = users.findOne(User::userName eq name)
    override suspend fun insertUser(user: User): Boolean = users.insertOne(user).wasAcknowledged()
    override suspend fun getAllUsers() = users.find().toList()
    override suspend fun getUserById(id: Long): User? = users.findOne(User::id eq id)
}