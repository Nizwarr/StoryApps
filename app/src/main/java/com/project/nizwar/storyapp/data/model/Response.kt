package com.project.nizwar.storyapp.data.model

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
