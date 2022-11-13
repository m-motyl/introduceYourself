package com.example.introduce_yourself.Models

data class ReadUserModel(
    val email: String,
    val description : String,
    val profile_picture: ByteArray
)