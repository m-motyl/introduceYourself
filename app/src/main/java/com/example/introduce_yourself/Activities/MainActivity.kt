package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.currentUser
import com.example.introduce_yourself.utils.saveImageByteArray
import com.google.android.material.navigation.NavigationView
import com.recyclerviewapp.UsersList
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MainActivity : AppCompatActivity(){
    private var readUserModelList = ArrayList<ReadUserModel>()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerLayout: View
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toggle : ActionBarDrawerToggle

    companion object{
        const val USER_DETAILS = "user_details"
        const val EXIT = "EXIT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
            when(it.itemId){
                R.id.nav_search -> Toast.makeText(applicationContext, "Clicked search", Toast.LENGTH_SHORT).show()
                R.id.nav_qr_scanner -> {
                    val intent = Intent(
                        this@MainActivity,
                        UserQrActivity::class.java
                    )
                    startActivity(intent)
                }
                R.id.nav_community -> Toast.makeText(applicationContext, "Clicked community", Toast.LENGTH_SHORT).show()
                R.id.nav_message -> Toast.makeText(applicationContext, "Clicked message", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(applicationContext, "Clicked settings", Toast.LENGTH_SHORT).show()
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

        getUsersList()
        if (readUserModelList.size > 0){
            usersRecyclerView(readUserModelList)
        }

        if(currentUser != null){
            headerLayout.nav_header_user_picture.setImageBitmap(byteArrayToBitmap(currentUser!!.profile_picture.bytes))
            headerLayout.nav_header_user_name.text = currentUser!!.name + " " + currentUser!!.surname
            headerLayout.nav_header_user_email.text = currentUser!!.email
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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