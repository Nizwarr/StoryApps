package com.project.nizwar.storyapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.project.nizwar.storyapp.data.model.DetailStoryResponse
import com.project.nizwar.storyapp.data.model.LoginResponse
import com.project.nizwar.storyapp.data.model.Response
import com.project.nizwar.storyapp.data.model.StoryResponse
import com.project.nizwar.storyapp.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class Repository private constructor(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
) {
    // Token dataStore
    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    // localization
    fun getLocaleSetting(): Flow<String> {
        return dataStore.data.map {
            it[LOCALE_KEY] ?: "en"
        }
    }

    suspend fun saveLocaleSetting(localeName: String) {
        dataStore.edit {
            it[LOCALE_KEY] = localeName
        }
    }

    // App function
    fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.login(email, password)
                saveToken(response.loginResult.token)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun register(name: String, email: String, password: String): LiveData<Result<Response>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getAllStories(token: String): LiveData<Result<StoryResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories("Bearer $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailStory(token: String, id: String): LiveData<Result<DetailStoryResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStories("Bearer $token", id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addNewStory(
        token: String,
        file: MultipartBody.Part,
        description: String
    ) : LiveData<Result<Response>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.addNewStory("Bearer $token", file, description.toRequestBody("text/plain".toMediaType()))
            emit(Result.Success(response))
        }  catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOCALE_KEY = stringPreferencesKey("locale")

        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            dataStore: DataStore<Preferences>
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(apiService, dataStore)
        }.also { instance = it }
    }
}