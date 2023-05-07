package com.project.nizwar.storyapp.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.nizwar.storyapp.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getToken() = repository.getToken().asLiveData(Dispatchers.IO)

    fun getAllStories(token: String) = repository.getAllStories(token)

    fun clearToken() {
        viewModelScope.launch {
            repository.clearToken()
        }
    }
}