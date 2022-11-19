package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class UserLink(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserLink>(UserLinks)

    var link by UserLinks.link
    var position by UserLinks.position
    var label by LinkLabel referencedOn UserLinks.label
    var user by User referencedOn UserLinks.user
}

object UserLinks : IntIdTable("UserLinks") {
    val link = varchar("link",100)
    val position = integer("position")
    val label = reference("label", LinkLabels, onDelete = ReferenceOption.CASCADE)
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
}