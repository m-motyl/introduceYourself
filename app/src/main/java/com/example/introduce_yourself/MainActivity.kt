package com.example.introduce_yourself

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.introduce_yourself.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    private fun connectToDb(){
        runBlocking {
            Database.connect(
                "jdbc:postgresql://10.0.2.2:5432/iydb", driver = "org.postgresql.Driver",
                user = "postgres", password = "123"
            )
            newSuspendedTransaction(Dispatchers.IO) {
                SchemaUtils.create(Users)
                SchemaUtils.create(Cities)
                SchemaUtils.create(Friends)
                SchemaUtils.create(DailyLikes)
                SchemaUtils.create(Messages)
                SchemaUtils.create(UserLikes)
                SchemaUtils.create(UserLinks)
                SchemaUtils.create(UserPosts)
            }
        }
    }
}