package com.example.advizors.models.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Login succeeded!\n\nWelcome to Advizors! "
    }
    val text: LiveData<String> = _text
}