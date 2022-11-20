package com.example.introduce_yourself.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.introduce_yourself.R
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_user_item.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setSupportActionBar(toolbar_edit_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_edit_profile.setNavigationOnClickListener {
            finish()
        }
        user_name_edit_btn.setOnClickListener(this)
        user_name_edit_save_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.user_name_edit_btn -> {
                edit_profile_user_name_tv.visibility = View.GONE
                edit_profile_user_name_et.visibility = View.VISIBLE
                user_name_edit_btn.visibility = View.GONE
                user_name_edit_save_btn.visibility = View.VISIBLE
            }
            R.id.user_name_edit_save_btn -> {
                edit_profile_user_name_et.visibility = View.GONE
                edit_profile_user_name_tv.visibility = View.VISIBLE
                user_name_edit_save_btn.visibility = View.GONE
                user_name_edit_btn.visibility = View.VISIBLE
            }
        }
    }
}