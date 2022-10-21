package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class UserPost(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserPost>(UserPosts)

    var content by UserPosts.content
    var title by UserPosts.title
    var user by User referencedOn UserPosts.user
}

object UserPosts : IntIdTable("UserPosts") {
    val content = varchar("amount",300)
    val title = varchar("amount",50)
    val user = reference("from", Users, onDelete = ReferenceOption.CASCADE)
}