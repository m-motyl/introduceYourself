package com.example.introduce_yourself.utils

import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.database.PostLike
import com.example.introduce_yourself.database.PostLikes
import com.example.introduce_yourself.database.UserPost
import com.example.introduce_yourself.database.UserPosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun readUserPosts(who: Int): ArrayList<UserPostModel> {
    val userPostsList = ArrayList<UserPostModel>()
    runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            val l = UserPost.find { UserPosts.user eq who }.toList()
            for (i in l) {
                var tmp = PostLike.find { PostLikes.post eq i.id }.groupBy { it.like }
                userPostsList.add(
                    UserPostModel(
                        post_title = i.title,
                        post_content = i.content,
                        date = i.date,
                        image = i.image!!.bytes,
                        likes = if (tmp[true] != null) tmp[true]!!.size else 0,
                        dislikes = if (tmp[false] != null) tmp[false]!!.size else 0,
                        id = i.id.value
                    )
                )
            }
        }
    }
    return userPostsList
}