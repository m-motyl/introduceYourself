package com.example.introduce_yourself.database

import com.example.introduce_yourself.database.Friends.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class Friend(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Friend>(Friends)

    var status by Friends.status
    var from by User referencedOn Friends.from
    var to by User referencedOn Friends.to

}

object Friends : IntIdTable("Friends") {
    val status = integer("status")
    val from = reference("from", Users, onDelete = ReferenceOption.CASCADE)
    val to = reference("to", Users, onDelete = ReferenceOption.CASCADE)
}