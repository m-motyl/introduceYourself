package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var readUserModel = ArrayList<ReadUserModel>()

    companion object{
        var USER_DETAILS = "user_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUsersList()
        if (readUserModel.size > 0){
            usersRecyclerView(readUserModel)
        }
    }
    private fun usersRecyclerView(readUserModel: ArrayList<ReadUserModel>){
        main_recycler_view.layoutManager = LinearLayoutManager(this)
        main_recycler_view.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModel)
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

    private fun getUsersList() {
        currentUser?.profile_picture?.let { ReadUserModel("mateusz", "motyl", it.bytes) }
            ?.let { readUserModel.add(it)
                    readUserModel.add(it)
                    readUserModel.add(it)
                    readUserModel.add(it)
                    readUserModel.add(it)
            } //test
    }//TODO: Witold wczytaj 5 userów i przypisz do readUserModel
}