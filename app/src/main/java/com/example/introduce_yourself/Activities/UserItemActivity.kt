package com.example.introduce_yourself.Activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.adapters.UserPostsAdapter
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.database.UserLink
import com.example.introduce_yourself.database.UserLinks
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.readUserPosts
import com.recyclerviewapp.UserLinksAdapter
import kotlinx.android.synthetic.main.activity_user_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserItemActivity : AppCompatActivity() {
    private var readUserModel: ReadUserModel? = null
    private var userLinksList = ArrayList<UserLinksModel>()
    private var userPostsList = ArrayList<UserPostModel>()
    private var stalked_user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_item)

        setSupportActionBar(toolbar_user_item)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_user_item.setNavigationOnClickListener {
            finish()
        }

        if (intent.hasExtra(MainActivity.USER_DETAILS)) {
            readUserModel = intent.getSerializableExtra(MainActivity.USER_DETAILS)
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
                usersLinksRecyclerView(userLinksList)
            } else {
                user_item_no_links_tv.visibility = View.VISIBLE
                user_item_links_recycler_view.visibility = View.GONE
            }

            userPostsList = readUserPosts(stalked_user!!.id.value)
            if (userPostsList.size > 0) {
                postsRecyclerView(userPostsList)
            } else {
                user_item_no_posts_tv.visibility = View.VISIBLE
                user_item_posts_recycler_view.visibility = View.GONE
            }

        }
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

}