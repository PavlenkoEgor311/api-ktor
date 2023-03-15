package com.example.data.model.user

import com.example.data.model.User
import com.example.data.model.user.request.UpdateUserFriendsRequest
import com.example.data.model.user.request.UpdateUserRequest
import com.example.security.hashing.HashingService
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult

interface IUserDataSource {
    suspend fun getUserName(name: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User?
    suspend fun updateUserData(user: UpdateUserRequest, hashingService: HashingService): UpdateResult
    suspend fun addFriend(id: Long, friendsRequest: UpdateUserFriendsRequest): UpdateResult
    suspend fun delFriend(id: Long, friend: UpdateUserFriendsRequest): UpdateResult
    suspend fun delUser(id: Long): DeleteResult
}