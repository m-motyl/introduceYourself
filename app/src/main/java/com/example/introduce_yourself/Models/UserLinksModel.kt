package com.example.introduce_yourself.Models

import java.io.Serializable

data class UserLinksModel( // cant be named same as files in /database
    val title: String,
    val link: String,
): Serializable