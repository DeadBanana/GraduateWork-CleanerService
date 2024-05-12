package com.example.cleaberservice.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedItems = MutableLiveData<ArrayList<String>>()
}