package com.example.introduce_yourself.Models


data class UserPostModel(
    val post_title: String,
    val post_content: String,
    val date: java.time.LocalDateTime,
    val image: ByteArray,
    val likes: Int? = 0,
    val dislikes: Int? = 0
)