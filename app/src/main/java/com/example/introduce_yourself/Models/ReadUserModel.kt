package com.example.introduce_yourself.Models

import java.io.Serializable

data class ReadUserModel(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val description: String,
    val profile_picture: ByteArray,
    val ranking: Int? = 0
//    val background_picture: ByteArray,
) : Serializable