package com.example.data.model.note

import com.example.data.model.GlobalNote
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import io.ktor.server.plugins.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.toList
import org.litote.kmongo.eq

class NoteDataSource(db: CoroutineDatabase) : IDataNoteSource {

    private val notes = db.getCollection<GlobalNote>("globalNote")

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

    override suspend fun getNotesUser(id: Long): List<GlobalNote> =
        notes.collection.find(GlobalNote::idParent eq id).toList()

    override suspend fun getNoteById(id: Long): GlobalNote? =
        notes.findOne(GlobalNote::id eq id)
}