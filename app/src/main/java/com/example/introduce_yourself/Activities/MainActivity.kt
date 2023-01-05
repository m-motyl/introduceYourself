package com.example.introduce_yourself.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.PostLike
import com.example.introduce_yourself.database.PostLikes
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.database.Users
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.currentUser
import com.google.android.material.navigation.NavigationView
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private var readUserModelList = ArrayList<ReadUserModel>()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerLayout: View
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        const val USER_DETAILS = "user_details"
        const val EXIT = "EXIT"
        const val THEME_CODE = 1
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
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById<DrawerLayout>(R.id.main_drawer_layout)
        navigationView = findViewById<NavigationView>(R.id.main_nav_view)
        headerLayout = navigationView.getHeaderView(0)
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)

        navigationView.bringToFront()
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_search -> {
                    val intent = Intent(
                        this@MainActivity,
                        SearchActivity::class.java
                    )
                    startActivity(intent)
                }
                R.id.nav_qr_scanner -> {
                    val intent = Intent(
                        this@MainActivity,
                        UserQrActivity::class.java
                    )
                    startActivity(intent)
                }
                R.id.nav_community -> {
                    val intent = Intent(
                        this@MainActivity,
                        CommunityActivity::class.java
                    )
                    startActivity(intent)
                }
                R.id.nav_message -> Toast.makeText(
                    applicationContext,
                    "Clicked message",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_settings -> {
                    val intent = Intent(
                        this@MainActivity,
                        SettingsActivity::class.java
                    )
                    startActivityForResult(intent, THEME_CODE)
                }
                R.id.nav_edit_profile -> {
                    val intent = Intent(
                        this@MainActivity,
                        EditProfileActivity::class.java
                    )
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra(EXIT, true)
                    startActivity(intent)
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCurrentUser()
        if (currentUser != null) {
            headerLayout.nav_header_user_picture.setImageBitmap(byteArrayToBitmap(currentUser!!.profile_picture.bytes))
            headerLayout.nav_header_user_name.text =
                currentUser!!.name + " " + currentUser!!.surname
            headerLayout.nav_header_user_email.text = currentUser!!.email
        }

        readUserModelList.clear()
        getUsersList()
        usersRecyclerView(ArrayList(readUserModelList.reversed()))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun usersRecyclerView(readUserModelList: ArrayList<ReadUserModel>) {
        main_recycler_view.layoutManager = LinearLayoutManager(this)
        main_recycler_view.setHasFixedSize(true)
        val usersList = UsersList(this, readUserModelList)
        main_recycler_view.adapter = usersList

        usersList.setOnClickListener(object : UsersList.OnClickListener {
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

    private fun getUsersList() =
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                val users = PostLike.find {
                    (PostLikes.time.greater(
                        LocalDateTime.now().minusDays(1)
                    )) and (PostLikes.like eq true)
                }.groupingBy { it.post.user }.eachCount().toList().sortedBy { it.second }.take(30)
                for (i in users)
                    readUserModelList.add(
                        ReadUserModel(
                            id = i.first.id.value,
                            name = i.first.name,
                            surname = i.first.surname,
                            email = i.first.email,
                            description = i.first.description,
                            profile_picture = i.first.profile_picture.bytes,
                            ranking = i.second
                        )
                    )
            }
        }


    private fun refreshCurrentUser() = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            currentUser = User.findById(currentUser!!.id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == THEME_CODE) {
            reloadActivity()
        }
    }

    private fun reloadActivity() {
        refreshCurrentUser()
        finish()
        startActivity(getIntent())
    }
}