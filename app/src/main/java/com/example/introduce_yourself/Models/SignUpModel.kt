package com.example.introduce_yourself.Models

data class SignUpModel(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val profile_picture: ByteArray,
    //val qr_code: String,
    //val description: String,
    //val background_picture: String,
    //val color_nr: Int,
    val city: String //todo trzeba miasta
)