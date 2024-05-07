package com.example.advizors.models.notes.delete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeleteUiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your post was deleted successfully"
    }
    val text: LiveData<String> = _text
}