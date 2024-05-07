package com.example.advizors.data.user

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.advizors.data.AppLocalDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class UserModel private constructor() {

    private val database = AppLocalDatabase.db
    private var usersExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = UserFirebaseModel()
    private val users: LiveData<MutableList<User>>? = null


    companion object {
        val instance: UserModel = UserModel()
    }


    fun getAllUsers(): LiveData<MutableList<User>> {
        refreshAllUsers()
        return users ?: database.userDao().getAll()
    }

    fun getCurrentUser(): LiveData<User> {
        refreshAllUsers()
        return database.userDao().getUserById(Firebase.auth.currentUser?.uid!!)
    }

    fun getUserById(id:String): LiveData<User> {
        refreshAllUsers()
        return database.userDao().getUserById(id)
    }

    fun refreshAllUsers() {
        val lastUpdated: Long = User.lastUpdated

        firebaseModel.getAllUsers(lastUpdated) { list ->
            var time = lastUpdated
            for (user in list) {
                firebaseModel.getImage(user.id) { uri ->
                    usersExecutor.execute {
                        user.profileImage = uri.toString()
                        database.userDao().insert(user)
                    }
                }

                user.lastUpdated?.let {
                    if (time < it)
                        time = user.lastUpdated ?: System.currentTimeMillis()
                }
                User.lastUpdated = time
            }
        }
    }


    fun updateUser(user: User?, callback: () -> Unit) {
        firebaseModel.updateUser(user) {
            refreshAllUsers()
            callback()
        }
    }

    fun updateUserImage(userId: String, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addUserImage(userId, selectedImageUri) {
            refreshAllUsers()
            callback()
        }
    }

    fun getUserImage(imageId: String, callback: (Uri) -> Unit) {
        firebaseModel.getImage(imageId, callback);
    }

    fun addUser(user: User, selectedImageUri: Uri?, callback: () -> Unit) {
        val addImageCallback: () -> Unit = {
            firebaseModel.addUserImage(user.id, selectedImageUri!!) {
                refreshAllUsers()
                callback()
            }
        }

        val addUserCallback: () -> Unit = {
            refreshAllUsers()
            callback()
        }

        if (selectedImageUri != null) {
            firebaseModel.addUser(user, addImageCallback)
        } else {
            firebaseModel.addUser(user, addUserCallback)
        }
    }

    fun logOff() {
        firebaseModel.signOffUser()
    }

}