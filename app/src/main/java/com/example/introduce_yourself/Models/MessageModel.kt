package com.example.introduce_yourself.Models

data class MessageModel (
    val text: String,
    val time: java.time.LocalDateTime,
    val user: Boolean
    )
