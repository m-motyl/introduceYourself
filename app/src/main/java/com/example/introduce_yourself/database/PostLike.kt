package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.datetime

class PostLike(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PostLike>(PostLikes)

    var like by PostLikes.like
    var time by PostLikes.time
    var user by User referencedOn PostLikes.user
    var post by UserPost referencedOn PostLikes.post
}

object PostLikes : IntIdTable("PostLikes") {
    val like = bool("like")
    val time = datetime("time")
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val post = reference("post", UserPosts, onDelete = ReferenceOption.CASCADE)
}