package com.example.advizors.models.my_notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.advizors.data.note.Note
import com.example.advizors.data.note.NoteModel

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
