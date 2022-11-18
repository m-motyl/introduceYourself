package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.google.android.material.navigation.NavigationView
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MainActivity : AppCompatActivity() {
    private var readUserModelList = ArrayList<ReadUserModel>()
    lateinit var toggle : ActionBarDrawerToggle

    companion object{
        var USER_DETAILS = "user_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout : DrawerLayout = findViewById(R.id.main_drawer_layout)
        val navView : NavigationView = findViewById(R.id.main_nav_view)
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> Toast.makeText(applicationContext, "Clicked home", Toast.LENGTH_SHORT).show()
                R.id.nav_search -> Toast.makeText(applicationContext, "Clicked search", Toast.LENGTH_SHORT).show()
                R.id.nav_qr_scanner -> Toast.makeText(applicationContext, "Clicked qr_scanner", Toast.LENGTH_SHORT).show()
                R.id.nav_message -> Toast.makeText(applicationContext, "Clicked message", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(applicationContext, "Clicked settings", Toast.LENGTH_SHORT).show()
                R.id.nav_edit_profile -> Toast.makeText(applicationContext, "Clicked edit_profile", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> Toast.makeText(applicationContext, "Clicked logout", Toast.LENGTH_SHORT).show()
            }
            true
        }

        getUsersList()
        if (readUserModelList.size > 0){
            usersRecyclerView(readUserModelList)
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
                    name = i.name,
                    surname = i.surname,
                    email = i.email,
                    description = "Opis Opis Opis Opis Opis Opis Opis Opis Opis Opis Opis Opis Opis ",
                    profile_picture = i.profile_picture.bytes
                )
            )
    }
}