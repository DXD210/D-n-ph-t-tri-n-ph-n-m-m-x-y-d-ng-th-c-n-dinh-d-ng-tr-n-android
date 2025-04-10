package com.example.nutritionapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutritionapp.database.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getNotesByUserId(userId: Long): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userId AND id = :noteId")
    fun getNoteById(userId: Long, noteId: Long): LiveData<Note>

    @Query("SELECT * FROM notes WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY updatedAt DESC")
    fun searchNotesByUserId(userId: Long, query: String): LiveData<List<Note>>

    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM notes WHERE userId = :userId AND id = :noteId")
    fun deleteNote(userId: Long, noteId: Long)
}