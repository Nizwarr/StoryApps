package com.project.nizwar.storyapp.view.register

import androidx.lifecycle.ViewModel
import com.project.nizwar.storyapp.data.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}