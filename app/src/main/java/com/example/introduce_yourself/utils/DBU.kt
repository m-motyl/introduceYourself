package com.example.introduce_yourself.utils

import android.util.Log
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.Collections.reverse

fun getUserLikes(id: Int): Int {
    return runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            PostLikes.innerJoin(UserPosts).slice(PostLikes.columns).select{
                (UserPosts.user eq id) and (PostLikes.time.greater(
                    LocalDateTime.now().minusDays(1)
                )) and (PostLikes.like eq true)}.withDistinct().count().toInt()
        }
    }
}