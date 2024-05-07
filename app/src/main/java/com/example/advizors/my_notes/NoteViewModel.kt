package com.example.advizors.my_notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.advizors.models.note.Note
import com.example.advizors.models.note.NoteModel

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteModel: NoteModel = NoteModel.instance

    private var myNotesLiveData: LiveData<MutableList<Note>>? = null

    fun getMyNotes(): LiveData<MutableList<Note>> {
        if (myNotesLiveData == null) {
            myNotesLiveData = noteModel.getMyNotes()
        }
        return myNotesLiveData!!
    }
}
