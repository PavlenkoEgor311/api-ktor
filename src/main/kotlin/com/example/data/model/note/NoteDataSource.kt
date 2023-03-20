package com.example.data.model.note

import com.example.data.core.concatenate
import com.example.data.model.GlobalNote
import com.example.data.model.User
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import io.ktor.server.plugins.*
import kotlinx.coroutines.coroutineScope
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq

class NoteDataSource(db: CoroutineDatabase) : IDataNoteSource {

    private val notes = db.getCollection<GlobalNote>("globalNote")
    private val users = db.getCollection<User>("user")

    override suspend fun getAllNotes(): List<GlobalNote> = notes.collection.find().toList()
    override suspend fun insertNote(note: GlobalNote): Boolean = notes.insertOne(note).wasAcknowledged()

    override suspend fun updateNote(newNote: GlobalNote): UpdateResult {
        val note = notes.findOne(GlobalNote::id eq newNote.id)
        if (note != null) {
            note.title = newNote.title
            note.description = newNote.description
            note.date = newNote.date
            note.status = newNote.status
            note.friendsId = newNote.friendsId
            note.files = newNote.files
            return notes.updateOne(
                filter = GlobalNote::id eq newNote.id,
                target = note,
                options = UpdateOptions().upsert(false)
            )
        } else throw NotFoundException()
    }

    override suspend fun getNotesUser(id: Long): List<GlobalNote> {
        val userNote = notes.collection.find(GlobalNote::idParent eq id).toList()
        val friendsCheckedNote = notes.collection.find(GlobalNote::friendsId.contains(id)).toList()
        return concatenate(
            userNote,
            friendsCheckedNote,
        )
    }


    override suspend fun getNoteById(id: Long): GlobalNote? =
        notes.findOne(GlobalNote::id eq id)

    override suspend fun addNewNote(idUser: Long, note: GlobalNote): UpdateResult {
        val user = users.findOne(User::id eq idUser)
        if (user != null) {
            insertNote(note)
            user.listIdNote.add(note.id)
            return users.updateOne(
                filter = User::id eq idUser,
                target = user,
                options = UpdateOptions().upsert(false),
            )
        } else throw NotFoundException()
    }


}