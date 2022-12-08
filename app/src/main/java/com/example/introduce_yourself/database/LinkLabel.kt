package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

class LinkLabel(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LinkLabel>(LinkLabels)

    var name by LinkLabels.name
}

object LinkLabels : IntIdTable("LinkLabels") {
    val name = varchar("name", 20)
}