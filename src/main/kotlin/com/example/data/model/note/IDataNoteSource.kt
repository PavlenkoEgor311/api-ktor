package com.example.data.model.note

import com.example.data.model.GlobalNote
import com.mongodb.client.result.UpdateResult

interface IDataNoteSource {
    suspend fun getAllNotes(): List<GlobalNote>
    suspend fun insertNote(note: GlobalNote): Boolean
    suspend fun updateNote(newNote: GlobalNote): UpdateResult
    suspend fun getNotesUser(id: Long): List<GlobalNote>
    suspend fun getNoteById(id: Long): GlobalNote?
    suspend fun addNewNote(idUser: Long, note: GlobalNote): UpdateResult
}