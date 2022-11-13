package com.example.introduce_yourself.Models

import java.io.Serializable

data class ReadUserModel(
    val email: String,
    val description : String,
    val profile_picture: ByteArray
): Serializable