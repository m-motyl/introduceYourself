package com.example.introduce_yourself.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.*
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UserLinksAdapter
import kotlinx.android.synthetic.main.activity_user_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserItemActivity : AppCompatActivity() {
    private var readUserModel: ReadUserModel? = null
    private var userLinksList = ArrayList<UserLinksModel>()
    private var background_image: ByteArray = ByteArray(0)
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
            supportActionBar!!.title = readUserModel!!.email
            user_item_user_picture.setImageBitmap(byteArrayToBitmap(readUserModel!!.profile_picture))
//            user_item_user_picture.setImageBitmap(byteArrayToBitmap(currentUser!!.profile_picture.bytes))
            user_item_user_name.text = readUserModel!!.name
            user_item_user_surname.text = readUserModel!!.surname
            user_item_user_email.text = readUserModel!!.email
            user_item_user_description.text = readUserModel!!.description
            readUserLinks()
            if (userLinksList.size > 0) {
                usersLinksRecyclerView(userLinksList)
            }
            if (stalked_user!!.background_picutre != null) { //TODO: Patryk create background in users profile
                background_image =
                    stalked_user!!.background_picutre!!.bytes // background_image to conversion
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

                var link = model.link
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://$link"

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(link)
                )
                startActivity(intent)
            }
        })
    }

    private fun readUserLinks() = runBlocking { // tmp method

        userLinksList.add(
            UserLinksModel("link1", "www.facebook.com")
        )
        userLinksList.add(
            UserLinksModel("link2", "www.youtube.com")
        )
    }

    // labels will have to be added to db manually im veri professional
    private fun readFullUser() = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            stalked_user = User.findById(readUserModel!!.id)
            val stalked_user_links = UserLink.find { UserLinks.user eq stalked_user!!.id }.toList()
            if (stalked_user_links.isNotEmpty())
                for (i in stalked_user_links)
                    userLinksList.add(UserLinksModel(title = i.label.name, link = i.link))
        }
    }
}