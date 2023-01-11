package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.Friend
import com.example.introduce_yourself.database.Friends
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import com.example.introduce_yourself.utils.getUserLikes
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import kotlin.collections.ArrayList

class MessagesActivity : AppCompatActivity() {
    private var friendsList = ArrayList<ReadUserModel>()
    private var offset: Long = 0L
    private var end_backward: Boolean = true
    private var end_forward: Boolean = true

    companion object {
        const val FRIEND_DETAILS = "user_details"
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
        setContentView(R.layout.activity_messages)

        setSupportActionBar(toolbar_messages)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_messages.setNavigationOnClickListener {
            finish()
        }

        friendsList = getCommunityList(currentUser!!.id.value, 1)
        friendsRecyclerView(friendsList)

    }
    private fun friendsRecyclerView(readUserModelList: ArrayList<ReadUserModel>) {
        messages_friends_list_rv.layoutManager = LinearLayoutManager(this)
        messages_friends_list_rv.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModelList)
        messages_friends_list_rv.adapter = usersList

        usersList.setOnClickListener(object : UsersList.OnClickListener {
            override fun onClick(position: Int, model: ReadUserModel) {
                val intent = Intent(
                    this@MessagesActivity,
                    DirectMessageActivity::class.java
                )

                intent.putExtra(
                    FRIEND_DETAILS,
                    model
                )

                startActivity(intent)
            }
        })
    }

    private fun getCommunityList( //TODO WITOLD read users current user wrote with
        who: Int,
        desired_status: Int,
        offset: Long = 0L
    ): ArrayList<ReadUserModel> {
        val usersList = ArrayList<ReadUserModel>()
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {

                var l = if (desired_status == 1) {
                    Friend.find {
                        ((Friends.from eq who) or (Friends.to eq who)) and
                                (Friends.status eq desired_status)
                    }.toList()
                } else {
                    Friend.find { (Friends.to eq who) and (Friends.status eq desired_status) }
                        .limit(6, offset).toList()
                }
                end_backward = offset == 0L
                end_forward = l.size < 6
                if (l.size == 6 && desired_status == 0)
                    l = l.dropLast(1)
                for (i in l) {
                    val tmp: User =
                        if (i.from.id.value == who && desired_status == 1) i.to else i.from
                    if (tmp.id.value != who)
                        usersList.add(
                            ReadUserModel(
                                id = tmp.id.value,
                                name = tmp.name,
                                surname = tmp.surname,
                                email = tmp.email,
                                description = tmp.description,
                                profile_picture = tmp.profile_picture.bytes,
                                ranking = getUserLikes(i.id.value)
                            )
                        )
                }
            }
        }

        if (end_forward)
            messages_next_friends.visibility = View.GONE
        else
            messages_next_friends.visibility = View.VISIBLE

        if (end_backward)
            messages_prev_friends.visibility = View.GONE
        else
            messages_prev_friends.visibility = View.VISIBLE

        this.offset = offset
        Collections.reverse(usersList)
        return usersList
    }

}