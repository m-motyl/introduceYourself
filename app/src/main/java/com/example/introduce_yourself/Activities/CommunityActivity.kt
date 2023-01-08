package com.example.introduce_yourself.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.adapters.UserInvitationsAdapter
import com.example.introduce_yourself.database.Friend
import com.example.introduce_yourself.database.Friends
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import com.example.introduce_yourself.utils.getUserLikes
//import com.example.introduce_yourself.utils.getCommunityList
//import com.example.introduce_yourself.utils.getCommunityList
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import java.util.Collections.reverse
import kotlin.collections.ArrayList

class CommunityActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val FRIEND_DETAILS = "user_details"
    }

    private var offset: Long = 0L
    private var end_backward: Boolean = true
    private var end_forward: Boolean = true
    private var friendsList = ArrayList<ReadUserModel>()
    private var invitationsList = ArrayList<ReadUserModel>()
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
        setContentView(R.layout.activity_community)

        setSupportActionBar(toolbar_community)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_community.setNavigationOnClickListener {
            finish()
        }

        friendsList = getCommunityList(currentUser!!.id.value, 1)
        friendsRecyclerView(friendsList)

        community_friends_list_button.setOnClickListener(this)
        community_invitations_list_button.setOnClickListener(this)
        community_prev_invitations.setOnClickListener(this)
        community_next_invitations.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.community_friends_list_button -> {
                if (community_friends_rv.visibility == View.GONE) {
                    community_friends_rv.visibility = View.VISIBLE
                    community_invitations_rv.visibility = View.GONE
                    Log.e("community", "friends")
                    friendsList.clear()
                    friendsList = getCommunityList(
                        currentUser!!.id.value,
                        1
                    )
                    friendsRecyclerView(friendsList)
                    community_invitations_loading_buttons.visibility = View.GONE

                    //changing buttons bg color
                    community_friends_list_button.setBackgroundColor(getThemeColor(R.attr.colorPrimaryVariant))
                    community_invitations_list_button.setBackgroundColor(getThemeColor(R.attr.colorPrimary))
                }
            }
            R.id.community_invitations_list_button -> {
                if (community_invitations_rv.visibility == View.GONE) {
                    community_friends_rv.visibility = View.GONE
                    community_invitations_rv.visibility = View.VISIBLE
                    Log.e("community", "inv")
                    invitationsList.clear()
                    invitationsList = getCommunityList(
                        currentUser!!.id.value,
                        0, offset
                    )
                    usersInvitationsRecyclerView(invitationsList)
                    community_invitations_loading_buttons.visibility = View.VISIBLE

                    //changing buttons bg color
                    community_invitations_list_button.setBackgroundColor(getThemeColor(R.attr.colorPrimaryVariant))
                    community_friends_list_button.setBackgroundColor(getThemeColor(R.attr.colorPrimary))
                }
            }
            R.id.community_prev_invitations -> {
                Log.e("prev", "inv")
                invitationsList = getCommunityList(currentUser!!.id.value, 0, offset - 5)
                usersInvitationsRecyclerView(invitationsList)
            }
            R.id.community_next_invitations -> {
                Log.e("next", "inv")
                invitationsList = getCommunityList(currentUser!!.id.value, 0, offset + 5)
                usersInvitationsRecyclerView(invitationsList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (community_friends_rv.visibility == View.VISIBLE) {
            friendsList.clear()
            friendsList = getCommunityList(currentUser!!.id.value, 1)
            friendsRecyclerView(friendsList)
        }
        Log.e("on", "resume")
    }

    private fun friendsRecyclerView(readUserModelList: ArrayList<ReadUserModel>) {
        community_friends_rv.layoutManager = LinearLayoutManager(this)
        community_friends_rv.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModelList)
        community_friends_rv.adapter = usersList

        usersList.setOnClickListener(object : UsersList.OnClickListener {
            override fun onClick(position: Int, model: ReadUserModel) {
                val intent = Intent(
                    this@CommunityActivity,
                    UserItemActivity::class.java
                )

                intent.putExtra(
                    FRIEND_DETAILS,
                    model
                )

                startActivity(intent)
            }
        })
    }

    private fun usersInvitationsRecyclerView(readUserInvitationsModelList: ArrayList<ReadUserModel>) {
        community_invitations_rv.layoutManager = LinearLayoutManager(this)
        community_invitations_rv.setHasFixedSize(true)
        val userInvitationsList = UserInvitationsAdapter(this, readUserInvitationsModelList)
        community_invitations_rv.adapter = userInvitationsList

        userInvitationsList.setOnClickListener(
            object : UserInvitationsAdapter.OnClickListener {
                override fun onClick(position: Int, model: ReadUserModel) {
                    Log.e("index", position.toString())
                }
            },
            object : UserInvitationsAdapter.OnAcceptClickListener {
                override fun onClick(position: Int, model: ReadUserModel) {
                    Log.e("accept index ", position.toString())
                    changeInvitationStatus(model, 1)

                    invitationsList = getCommunityList(
                        currentUser!!.id.value,
                        0, offset
                    )
                    usersInvitationsRecyclerView(invitationsList)
                }
            },
            object : UserInvitationsAdapter.OnRejectClickListener {
                override fun onClick(position: Int, model: ReadUserModel) {
                    Log.e("reject index ", position.toString())
                    changeInvitationStatus(model, -1)

                    invitationsList = getCommunityList(
                        currentUser!!.id.value,
                        0, offset
                    )
                    usersInvitationsRecyclerView(invitationsList)
                }
            }
        )
    }

    private fun changeInvitationStatus(model: ReadUserModel, new: Int) =
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                val x =
                    Friend.find { (Friends.to eq currentUser!!.id) and (Friends.from eq model.id) }
                        .firstOrNull()
                if (x != null)
                    when (new) {
                        1 -> x.status = 1
                        -1 -> x.delete()
                    }
            }
        }

    private fun getCommunityList(
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
            community_next_invitations.visibility = View.GONE
        else
            community_next_invitations.visibility = View.VISIBLE

        if (end_backward)
            community_prev_invitations.visibility = View.GONE
        else
            community_prev_invitations.visibility = View.VISIBLE

        this.offset = offset
        reverse(usersList)
        return usersList
    }

    @ColorInt
    fun Context.getThemeColor(
        @AttrRes attribute: Int
    ) = TypedValue().let {
        theme.resolveAttribute(attribute, it, true);
        it.data
    }
}