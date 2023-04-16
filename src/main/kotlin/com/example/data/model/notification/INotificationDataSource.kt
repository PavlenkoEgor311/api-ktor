package com.example.data.model.notification

import com.example.data.model.Notification
import com.mongodb.client.result.DeleteResult

interface INotificationDataSource {
    suspend fun insertNotification(notification: Notification): Boolean
    suspend fun deleteNotification(notification: Notification): DeleteResult
    suspend fun getUserNotification(userId: Long)
    suspend fun getNotificationById(notificationId: Long)
}