package com.example.introduce_yourself.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.introduce_yourself.R
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_user_item.*

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setSupportActionBar(toolbar_edit_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_edit_profile.setNavigationOnClickListener {
            finish()
        }
    }
}