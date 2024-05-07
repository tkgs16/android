package com.example.advizors.models.note

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.advizors.models.note.Note

@Dao
interface NoteDao {
    @Query("select * from Note")
    fun getAllNotes(): LiveData<MutableList<Note>>

    @Query("SELECT * FROM note WHERE userId = :userId")
    fun getNotesByUserId(userId: String): LiveData<MutableList<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)
}