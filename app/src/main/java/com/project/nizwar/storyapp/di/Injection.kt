package com.project.nizwar.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.project.nizwar.storyapp.data.Repository
import com.project.nizwar.storyapp.data.network.ApiConfig


val Context.dataStore: DataStore<Preferences> by preferencesDataStore("preferences")

object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService, context.dataStore)
    }
}