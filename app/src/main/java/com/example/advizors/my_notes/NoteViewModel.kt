package com.example.advizors.my_notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.advizors.models.note.Note
import com.example.advizors.models.note.NoteModel

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteModel: NoteModel = NoteModel.instance

    // LiveData to hold the list of notes for the current user
    private var myNotesLiveData: LiveData<MutableList<Note>>? = null

    // Function to get the notes for the current user
    fun getMyNotes(): LiveData<MutableList<Note>> {
        if (myNotesLiveData == null) {
            myNotesLiveData = noteModel.getMyNotes()
        }
        return myNotesLiveData!!
    }
}
