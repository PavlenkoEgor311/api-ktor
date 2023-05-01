package com.example.data.model.notification

import com.example.data.model.Notification
import com.mongodb.client.result.DeleteResult
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class NotificationDataSource(db: CoroutineDatabase) : INotificationDataSource {

    private val notifications = db.getCollection<Notification>("notifications")

    override suspend fun insertNotification(notification: Notification): Boolean =
        notifications.insertOne(notification).wasAcknowledged()

    override suspend fun deleteNotification(notification: Notification): DeleteResult =
        notifications.deleteOne(Notification::id eq notification.id)

    override suspend fun getUserNotification(userId: Long) {
        notifications.find(Notification::userId eq userId)
    }

    override suspend fun getNotificationById(notificationId: Long) {
        notifications.findOne(Notification::id eq notificationId)
    }
}