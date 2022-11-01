package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var surname by Users.surname
    var email by Users.email
    var password by Users.password
    var profile_picture by Users.profile_picture
    var qr_code by Users.qr_code
    var description by Users.description
    var background_picutre by Users.background_picture
    var color_nr by Users.color_nr
    var city by City referencedOn Users.city

}

object Users : IntIdTable("Users") {
    val name = varchar("name", 30)
    val surname = varchar("surename", 30)
    val email = varchar("email", 50)
    val password = varchar("password", 20)
    val profile_picture = blob("profile_picture").nullable()
    val qr_code = varchar("qr_code", 500)
    val description = varchar("description", 1000)
    val background_picture = blob("background_picture").nullable()
    val color_nr = integer("color_nr")
    val city = reference("city", Cities, onDelete = ReferenceOption.SET_NULL)
}