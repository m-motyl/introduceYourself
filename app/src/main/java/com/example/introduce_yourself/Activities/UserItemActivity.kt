package com.example.introduce_yourself.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import kotlinx.android.synthetic.main.activity_user_item.*

class UserItemActivity : AppCompatActivity() {
    var readUserModel: ReadUserModel? = null
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
            supportActionBar!!.title = readUserModel!!.email
        }

    }
}