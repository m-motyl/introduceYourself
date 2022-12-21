package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_user_item.*
import kotlinx.android.synthetic.main.activity_user_item.toolbar_user_item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private var readUserModelList = ArrayList<ReadUserModel>()
    companion object {
        const val USER_DETAILS = "user_details"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        when (currentUser!!.color_nr) {
            0 -> {
                setTheme(R.style.Theme0_Introduce_yourself)
            }
            1 -> {
                setTheme(R.style.Theme1_Introduce_yourself)
            }
            2 -> {
                setTheme(R.style.Theme2_Introduce_yourself)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar_search)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        toolbar_search.setNavigationOnClickListener {
            finish()
        }

        getUsersList()
        usersRecyclerView(readUserModelList)

        search_find_btn.setOnClickListener(this)
        search_prev_users.setOnClickListener(this)
        search_next_users.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.search_find_btn -> {
                if(search_find_et.text.toString().isNotEmpty()){
                    if(isEmailValid(search_find_et.text.toString())) {
                        Log.e("email", "ok")
                        findUserByEmail(search_find_et.text.toString())
                    }
                    else{
                        Log.e("to nie ", "email")
                        findUserByName(search_find_et.text.toString())
                    }
                }else{
                    Log.e("pusta", "lista")
                    readUserModelList.clear()
                    usersRecyclerView(readUserModelList)
                }
            }
            R.id.search_prev_users -> { //TODO WITOLD search pagination
                Log.e("prev", "posts")
            }
            R.id.search_next_users -> {
                Log.e("next", "posts")
            }
        }
    }

    private fun findUserByName(toString: String) { //TODO WITOLD find user by name

    }

    private fun findUserByEmail(toString: String) { //TODO WITOLD find user by email

    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun usersRecyclerView(readUserModelList: ArrayList<ReadUserModel>) {
        search_recycler_view.layoutManager = LinearLayoutManager(this)
        search_recycler_view.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModelList)
        search_recycler_view.adapter = usersList

        usersList.setOnClickListener(object : UsersList.OnClickListener {
            override fun onClick(position: Int, model: ReadUserModel) {
                val intent = Intent(
                    this@SearchActivity,
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

    private fun getUsersList() = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            val list = User.all().limit(5).toList()
            if (list.isNotEmpty())
                exposedToModel(list)
        }
    }

    private fun exposedToModel(list: List<User>) {
        for (i in list)
            readUserModelList.add(
                ReadUserModel(
                    id = i.id.value,
                    name = i.name,
                    surname = i.surname,
                    email = i.email,
                    description = i.description,
                    profile_picture = i.profile_picture.bytes
                )
            )
    }
}