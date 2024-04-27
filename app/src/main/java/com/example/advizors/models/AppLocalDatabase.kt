package com.example.advizors.models

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advizors.Advizors
import com.example.advizors.models.user.User
import com.example.advizors.models.user.UserDAO


@Database(entities = [User::class], version = 7, exportSchema = true)
//@TypeConverters(LatLngConverter::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
//    abstract fun postDao(): PostDAO
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