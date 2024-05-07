package com.example.advizors.data.note

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class NoteFirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val NOTES_COLLECTION_PATH = "notes"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }


    fun getAllNotes(since: Long, callback: (List<Note>) -> Unit) {

        db.collection(NOTES_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(Note.LAST_UPDATED_KEY, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val notes: MutableList<Note> = mutableListOf()
                        for (json in it.result) {
                            val student = Note.fromJSON(json.data)
                            notes.add(student)
                        }
                        callback(notes)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getImage(imageId: String, callback: (Uri) -> Unit) {
        storage.reference.child("images/notes/$imageId")
            .downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri)
            }
    }

    fun addNote(note: Note, callback: () -> Unit) {
        db.collection(NOTES_COLLECTION_PATH).document(note.id).set(note.json)
            .addOnSuccessListener {
                callback()
            }
    }

    fun addNoteImage(noteId: String, selectedImageUri: Uri, callback: () -> Unit) {
        val imageRef = storage.reference.child("images/$NOTES_COLLECTION_PATH/${noteId}")
        imageRef.putFile(selectedImageUri).addOnSuccessListener {
            callback()
        }
    }

    fun deleteNote(note: Note?, callback: () -> Unit) {
        db.collection(NOTES_COLLECTION_PATH)
            .document(note!!.id).update(note.deleteJson).addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't delete this note document: " + it.message)
            }
    }

    fun updateNote(note: Note?, callback: () -> Unit) {
        db.collection(NOTES_COLLECTION_PATH)
            .document(note!!.id).update(note.updateJson)
            .addOnSuccessListener {
                callback()
            }.addOnFailureListener {
                Log.d("Error", "Can't update this note document: " + it.message)
            }
    }
}