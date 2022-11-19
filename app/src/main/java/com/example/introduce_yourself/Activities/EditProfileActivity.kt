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
        button_change_visibility.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.button_change_visibility -> { //TODO: Patryk change visibility of layout items
                if(textView2.visibility != View.VISIBLE ){
                    textView.visibility = View.GONE
                    textView2.visibility = View.VISIBLE
                }
                else{
                    textView2.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                }
            }
        }
    }
}