package com.example.introduce_yourself.Models

data class SignUpModel(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val profile_picture: ByteArray,
)