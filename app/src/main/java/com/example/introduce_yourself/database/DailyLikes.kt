package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class DailyLike(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DailyLike>(DailyLikes)

    var amount by DailyLikes.amount
    var user by User referencedOn DailyLikes.user
}

object DailyLikes : IntIdTable("DailyLikes") {
    val amount = integer("amount")
    val user = reference("from", Users, onDelete = ReferenceOption.CASCADE)
}