package com.project.nizwar.storyapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.nizwar.storyapp.data.Repository
import com.project.nizwar.storyapp.di.Injection
import com.project.nizwar.storyapp.view.detail.DetailViewModel
import com.project.nizwar.storyapp.view.login.LoginViewModel
import com.project.nizwar.storyapp.view.main.MainViewModel
import com.project.nizwar.storyapp.view.post.PostViewModel
import com.project.nizwar.storyapp.view.register.RegisterViewModel
import com.project.nizwar.storyapp.view.settings.SettingsViewModel

class ViewModelFactory private constructor(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(repository) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> return MainViewModel(repository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> return RegisterViewModel(repository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> return DetailViewModel(repository) as T
            modelClass.isAssignableFrom(PostViewModel::class.java) -> return PostViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(Injection.provideRepository(context))
        }.also { instance = it }
    }
}