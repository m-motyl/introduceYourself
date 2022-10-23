package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.datetime

class UserLike(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserLike>(UserLikes)

    var time by UserLikes.time
    var from by User referencedOn UserLikes.from
    var to by User referencedOn UserLikes.to
}

object UserLikes : IntIdTable("UserLikes") {
    val time = datetime("time_sent")
    val from = reference("from", Users, onDelete = ReferenceOption.CASCADE)
    val to = reference("to", Users, onDelete = ReferenceOption.CASCADE)
}