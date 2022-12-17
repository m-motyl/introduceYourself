package com.example.introduce_yourself.utils

import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.Collections.reverse

fun getCommunityList(who: Int, desired_status: Int): ArrayList<ReadUserModel> {
    val usersList = ArrayList<ReadUserModel>()
    runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            val l =
                Friend.find {
                    ((Friends.from eq who) or (Friends.to eq who)) and
                            (Friends.status eq desired_status)
                }.toList()
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