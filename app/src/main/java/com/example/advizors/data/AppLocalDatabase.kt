package com.example.advizors.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advizors.Advizors
import com.example.advizors.data.note.LatLngConverter
import com.example.advizors.data.note.NoteDao
import com.example.advizors.data.user.User
import com.example.advizors.data.note.Note
import com.example.advizors.data.user.UserDAO


@Database(entities = [User::class, Note::class], version = 9, exportSchema = false)
@TypeConverters(LatLngConverter::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun noteDao(): NoteDao
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = Advizors.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "advizors"
        ).fallbackToDestructiveMigration()
            .build()
    }
}