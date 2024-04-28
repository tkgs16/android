package com.example.advizors.models.note

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.advizors.models.AppLocalDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class NoteModel private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    companion object {
        val instance: NoteModel = NoteModel()
    }

    private val database = AppLocalDatabase.db
    private var notesExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = NoteFirebaseModel()
    private val notes: LiveData<MutableList<Note>>? = null
    val notesListLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.LOADED)



    fun getAllNotes(): LiveData<MutableList<Note>> {
        refreshNotes()
        return notes ?: database.noteDao().getAllNotes()
    }

    fun getMyNotes(): LiveData<MutableList<Note>> {
        refreshNotes()
        return notes ?: database.noteDao().getNotesByUserId(Firebase.auth.currentUser?.uid!!)
    }

    private fun refreshNotes() {
        notesListLoadingState.value = LoadingState.LOADING

        val lastUpdated: Long = Note.lastUpdated

        firebaseModel.getAllNotes(lastUpdated) { list ->
            var time = lastUpdated
            for (note in list) {
                if (note.isDeleted) {
                    notesExecutor.execute {
                        database.noteDao().delete(note)
                    }
                } else {
                    firebaseModel.getImage(note.id) { uri ->
                        notesExecutor.execute {
                            note.imageUrl = uri.toString()
                            database.noteDao().insert(note)
                        }
                    }

                    note.timestamp?.let {
                        if (time < it)
                            time = note.timestamp ?: System.currentTimeMillis()
                    }
                    Note.lastUpdated = time
                }
            }
            notesListLoadingState.postValue(LoadingState.LOADED)
        }
    }

    fun addNote(note: Note, selectedImageUri: Uri, callback:(() -> Unit)? = null) {
        firebaseModel.addNote(note) {
            firebaseModel.addNoteImage(note.id, selectedImageUri) {
                refreshNotes()
                if (callback != null) {
                    callback()
                }
            }
        }
    }

    fun deleteNote(note: Note, callback: () -> Unit) {
        firebaseModel.deleteNote(note) {
            refreshNotes()
            callback()
        }
    }

    fun updateNote(note: Note?, callback: () -> Unit) {
        firebaseModel.updateNote(note) {
            refreshNotes()
            callback()
        }
    }

    fun updateNoteImage(noteId: String, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addNoteImage(noteId, selectedImageUri) {
            refreshNotes()
            callback()
        }
    }

    fun getNoteImage(imageId: String, callback: (Uri) -> Unit) {
        firebaseModel.getImage(imageId, callback);
    }
}