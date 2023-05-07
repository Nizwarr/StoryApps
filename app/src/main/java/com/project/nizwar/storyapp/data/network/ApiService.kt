package com.project.nizwar.storyapp.data.network


import com.project.nizwar.storyapp.data.model.DetailStoryResponse
import com.project.nizwar.storyapp.data.model.LoginResponse
import com.project.nizwar.storyapp.data.model.Response
import com.project.nizwar.storyapp.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response
}