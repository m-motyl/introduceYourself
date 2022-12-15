package com.example.introduce_yourself.utils

import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.Collections.reverse

fun readUserPosts(who: Int): ArrayList<UserPostModel> {
    val userPostsList = ArrayList<UserPostModel>()
    runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            val l = UserPost.find { UserPosts.user eq who }.toList()
            for (i in l) {
                val tmp = PostLike.find { PostLikes.post eq i.id }.groupBy { it.like }
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
    reverse(userPostsList)
    return userPostsList
}

fun getCommunityList(who: Int, desired_status: Int): ArrayList<ReadUserModel> {
    val usersList = ArrayList<ReadUserModel>()
    runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            val l =
                Friend.find { ((Friends.from eq who) or (Friends.to eq who)) and
                        (Friends.status eq desired_status) }.toList()
            for (i in l) {
                val tmp: User =
                    if (i.from.id.value == who && desired_status == 1) i.to else i.from
                usersList.add(
                    ReadUserModel(
                        id = tmp.id.value,
                        name = tmp.name,
                        surname = tmp.surname,
                        email = tmp.email,
                        description = tmp.description,
                        profile_picture = tmp.profile_picture.bytes
                    )
                )
            }
        }
    }
    reverse(usersList)
    return usersList
}

fun readNextFivePosts(who: Int): ArrayList<UserPostModel> { //TODO WITOLD read 5 posts, offset fun argument??
    val userPostsList = ArrayList<UserPostModel>()
    return ArrayList(userPostsList.reversed())
}