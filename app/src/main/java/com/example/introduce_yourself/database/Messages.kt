package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.datetime

class Message(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Message>(Messages)

    var content by Messages.content
    var time by Messages.time
    var from by User referencedOn Messages.from
    var to by User referencedOn Messages.to
}

object Messages : IntIdTable("Messages") {
    val content = varchar("amount", 500)
    val time = datetime("time_sent")
    val from = reference("from", Users, onDelete = ReferenceOption.CASCADE)
    val to = reference("to", Users, onDelete = ReferenceOption.CASCADE)
}