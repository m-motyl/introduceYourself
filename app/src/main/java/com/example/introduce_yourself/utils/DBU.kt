package com.example.introduce_yourself.utils

import android.util.Log
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.Collections.reverse

//remnant of the past