package com.example.introduce_yourself.Activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.adapters.UserPostsAdapter
import com.example.introduce_yourself.database.*
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UserLinksAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_user_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import java.util.Collections.reverse
import kotlin.collections.ArrayList

class UserItemActivity : AppCompatActivity(), View.OnClickListener {
    private var offset: Long = 0L
    private var end_backward: Boolean = true
    private var end_forward: Boolean = true
    private var readUserModel: ReadUserModel? = null
    private var userLinksList = ArrayList<UserLinksModel>()
    private var initLinksList = ArrayList<UserLinksModel>()

    private var userPostsList = ArrayList<UserPostModel>()

    private var stalked_user: User? = null

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
        setContentView(R.layout.activity_user_item)

        setSupportActionBar(toolbar_user_item)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_user_item.setNavigationOnClickListener {
            finish()
        }
        if (intent.hasExtra(CommunityActivity.FRIEND_DETAILS)) {
            readUserModel = intent.getSerializableExtra(CommunityActivity.FRIEND_DETAILS)
                    as ReadUserModel
        }
        if (intent.hasExtra(MainActivity.USER_DETAILS)) {
            readUserModel = intent.getSerializableExtra(MainActivity.USER_DETAILS)
                    as ReadUserModel
        }
        if (intent.hasExtra(UserQrActivity.QR_USER_FOUND)) {
            readUserModel = intent.getSerializableExtra(UserQrActivity.QR_USER_FOUND)
                    as ReadUserModel
        }

        if (readUserModel != null) {
            readFullUser()

            if (stalked_user != null) {
                supportActionBar!!.title = stalked_user!!.email
                user_item_user_picture.setImageBitmap(byteArrayToBitmap(stalked_user!!.profile_picture.bytes))
                if (stalked_user!!.background_picutre != null) {
                    user_item_bg_picture.setImageBitmap(byteArrayToBitmap(stalked_user!!.background_picutre!!.bytes))
                }
                user_item_user_name.text = stalked_user!!.name
                user_item_user_surname.text = stalked_user!!.surname
                user_item_user_email.text = stalked_user!!.email
                user_item_user_description.text = stalked_user!!.description
            }

            if (userLinksList.size > 0) {
                initLinksList.add(userLinksList[0])
                usersLinksRecyclerView(initLinksList)
                if (userLinksList.size < 2) {
                    user_item_links_expand_more.visibility = View.GONE
                }
            } else {
                user_item_no_links_tv.visibility = View.VISIBLE
                user_item_links_recycler_view.visibility = View.GONE
                user_item_links_expand_more.visibility = View.GONE
            }

            userPostsList = readUserPosts(stalked_user!!.id.value, 0)
            if (userPostsList.size > 0) {
                postsRecyclerView(userPostsList)
            } else {
                user_item_prev_posts.visibility = View.GONE
                user_item_next_posts.visibility = View.GONE
                user_item_no_posts_tv.visibility = View.VISIBLE
                user_item_posts_recycler_view.visibility = View.GONE
            }

            if (currentUser!!.id.value == readUserModel!!.id) {
                user_item_invite_user.visibility = View.GONE
            }

            if (checkIfAlreadyFriends(readUserModel!!.id)) {
                user_item_invite_user.visibility = View.GONE
                user_item_remove_user.visibility = View.VISIBLE
            }
        }
        user_item_links_expand_more.setOnClickListener(this)
        user_item_links_expand_less.setOnClickListener(this)
        user_item_invite_user.setOnClickListener(this)
        user_item_remove_user.setOnClickListener(this)
        user_item_prev_posts.setOnClickListener(this)
        user_item_next_posts.setOnClickListener(this)
    }

    private fun usersLinksRecyclerView(userLinks: ArrayList<UserLinksModel>) {
        user_item_links_recycler_view.layoutManager = LinearLayoutManager(this)
        user_item_links_recycler_view.setHasFixedSize(true)
        val userLinks = UserLinksAdapter(this, userLinks)
        user_item_links_recycler_view.adapter = userLinks

        userLinks.setOnClickListener(object : UserLinksAdapter.OnClickListener {
            override fun onClick(position: Int, model: UserLinksModel) {
                val alert = AlertDialog.Builder(this@UserItemActivity)
                alert.setTitle("Czy chcesz otworzyć ${model.link}?")
                val items = arrayOf(
                    "Tak",
                    "Nie"
                )
                alert.setItems(items) { _, n ->
                    when (n) {
                        0 -> {
                            var link = model.link
                            if (!link.startsWith("http://") && !link.startsWith("https://"))
                                link = "http://$link"

                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(link)
                            )
                            startActivity(intent)
                        }
                        1 -> {}
                    }
                }
                alert.show()
            }
        })
    }

    private fun postsRecyclerView(userPosts: ArrayList<UserPostModel>) {

        user_item_posts_recycler_view.layoutManager = LinearLayoutManager(this)
        user_item_posts_recycler_view.setHasFixedSize(true)
        val userPosts = UserPostsAdapter(this, userPosts)
        user_item_posts_recycler_view.adapter = userPosts

        userPosts.setOnClickListener(object : UserPostsAdapter.OnClickListener {
            override fun onClick(position: Int, model: UserPostModel) {
            }
        })
    }

    private fun readFullUser() = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            stalked_user = User.findById(readUserModel!!.id)
            val stalked_user_links = UserLink.find { UserLinks.user eq stalked_user!!.id }
                .orderBy(UserLinks.position to SortOrder.ASC).toList()
            if (stalked_user_links.isNotEmpty())
                for (i in stalked_user_links)
                    userLinksList.add(UserLinksModel(title = i.label.name, link = i.link))
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.user_item_prev_posts -> {
                userPostsList = readUserPosts(stalked_user!!.id.value, offset - 5)
                postsRecyclerView(userPostsList)
            }
            R.id.user_item_next_posts -> {
                userPostsList = readUserPosts(stalked_user!!.id.value, offset + 5)
                postsRecyclerView(userPostsList)
            }
            R.id.user_item_remove_user -> {
                val alert = AlertDialog.Builder(this@UserItemActivity)
                alert.setTitle("Czy chcesz usunąć znajomego?")
                val items = arrayOf(
                    "Tak",
                    "Nie"
                )
                alert.setItems(items) { _, n ->
                    when (n) {
                        0 -> {
                            removeFriend(readUserModel)
                            user_item_invite_user.visibility = View.VISIBLE
                            user_item_remove_user.visibility = View.GONE
                        }
                        1 -> {}
                    }
                }
                alert.show()
            }
            R.id.user_item_links_expand_more -> {
                usersLinksRecyclerView(userLinksList)

                user_item_links_expand_more.visibility = View.GONE
                user_item_links_expand_less.visibility = View.VISIBLE
            }
            R.id.user_item_links_expand_less -> {
                usersLinksRecyclerView(initLinksList)

                user_item_links_expand_more.visibility = View.VISIBLE
                user_item_links_expand_less.visibility = View.GONE
            }
            R.id.user_item_invite_user -> {
                val res = inviteFriend(readUserModel!!.id)
                when (res) {
                    "friend_limit" -> {
                        Toast.makeText(
                            this,
                            "Osiągnięto limit znajomych! (50)",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("limit", "limit")
                    }
                    "already_sent" -> {
                        Toast.makeText(
                            this,
                            "Zaproszenie zostało wysłane już wcześniej",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("sent", "sent")
                    }
                    "ok" -> {
                        Toast.makeText(
                            this,
                            "Zaproszenie zostało wysłane",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("ok", "ok")
                    }
                }
            }
        }
    }

    private fun removeFriend(who: ReadUserModel?) = runBlocking {
        if (who != null)
            newSuspendedTransaction(Dispatchers.IO) {
                Friend.find {
                    (((Friends.from eq who.id) and (Friends.to eq currentUser!!.id)) or
                            ((Friends.to eq who.id) and (Friends.from eq currentUser!!.id))) and
                            (Friends.status eq 1)
                }.firstOrNull()!!.delete()
            }
    }

    private fun inviteFriend(who: Int): String {
        return runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                when {
                    Friend.find {
                        (((Friends.from eq currentUser!!.id) and (Friends.to eq who)) or
                                ((Friends.to eq currentUser!!.id) and
                                        (Friends.from eq who))) and (Friends.status eq 0)
                    }.toList().isNotEmpty() -> "already_sent"
                    Friend.find {
                        ((Friends.from eq currentUser!!.id) or
                                (Friends.to eq currentUser!!.id)) and (Friends.status eq 1)
                    }
                        .count() > 50 -> "friend_limit"
                    else -> {
                        Friend.new {
                            status = 0
                            from = currentUser!!
                            to = User.findById(who)!!
                        }
                        "ok"
                    }
                }

            }
        }
    }


    private fun checkIfAlreadyFriends(who: Int): Boolean {
        return runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                Friend.find {
                    (((Friends.from eq currentUser!!.id) and (Friends.to eq who)) or
                            ((Friends.to eq currentUser!!.id) and (Friends.from eq who))) and
                            (Friends.status eq 1)
                }.toList().isNotEmpty()
            }
        }
    }

    private fun readUserPosts(who: Int, offset: Long): ArrayList<UserPostModel> {
        val userPostsList = ArrayList<UserPostModel>()
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                var l = UserPost.find { UserPosts.user eq who }
                    .orderBy(UserPosts.date to SortOrder.DESC).limit(6, offset).toList()
                end_backward = offset == 0L
                end_forward = l.size < 6
                if (l.size == 6)
                    l = l.dropLast(1)
                for (i in l) {
                    val tmp = PostLike.find { PostLikes.post eq i.id }.groupBy { it.like }
                    userPostsList.add(
                        UserPostModel(
                            post_title = i.title,
                            post_content = i.content,
                            date = i.date,
                            image = i.image!!.bytes,
                            likes = if (tmp[true] != null) tmp[true]!!.size else 0,
                            dislikes = if (tmp[false] != null) tmp[false]!!.size else 0,
                            id = i.id.value
                        )
                    )
                }
            }
        }
        if (end_forward) {
            user_item_next_posts.visibility = View.GONE
        } else {
            user_item_next_posts.visibility = View.VISIBLE
        }

        if (end_backward) {
            user_item_prev_posts.visibility = View.GONE
        } else {
            user_item_prev_posts.visibility = View.VISIBLE
        }

        user_item_nested_scroll_view.scrollTo(
            0,
            user_item_image_rl.height +
                    user_item_user_info_ll.height +
                    user_item_description_ll.height
        )

        this.offset = offset
        return userPostsList
    }
}