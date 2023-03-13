package com.example.data.model.user

import com.example.data.model.User

interface IUserDataSource {

    suspend fun getUserName(name: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User?
}