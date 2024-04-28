package com.example.advizors.maps

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapsViewModel : ViewModel() {
    var location: MutableLiveData<Location> = MutableLiveData();

}
