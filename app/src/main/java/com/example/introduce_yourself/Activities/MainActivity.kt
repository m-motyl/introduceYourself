package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MainActivity : AppCompatActivity() {
    private var readUserModelList = ArrayList<ReadUserModel>()

    companion object{
        var USER_DETAILS = "user_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUsersList()
        if (readUserModelList.size > 0){
            usersRecyclerView(readUserModelList)
        }
    }
    private fun usersRecyclerView(readUserModelList: ArrayList<ReadUserModel>){
        main_recycler_view.layoutManager = LinearLayoutManager(this)
        main_recycler_view.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModelList)
        main_recycler_view.adapter = usersList

        usersList.setOnClickListener(object : UsersList.OnClickListener{
            override fun onClick(position: Int, model: ReadUserModel) {
                val intent = Intent(
                    this@MainActivity,
                    UserItemActivity::class.java
                )

                intent.putExtra(
                    USER_DETAILS,
                    model
                )

                startActivity(intent)
            }
        })
    }

    private fun getUsersList() = runBlocking{
        newSuspendedTransaction(Dispatchers.IO) {
            val list = User.all().limit(5).toList()
            if (list.isNotEmpty())
                exposedToModel(list)
        }
    }
    private fun exposedToModel(list: List<User>){
        for(i in list)
            readUserModelList.add(
                ReadUserModel(
                    email = i.email,
                    description = i.description,
                    profile_picture = i.profile_picture.bytes
                )
            )
    }
}