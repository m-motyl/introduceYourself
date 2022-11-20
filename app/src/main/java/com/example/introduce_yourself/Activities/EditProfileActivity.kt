package com.example.introduce_yourself.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UserLinksAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_user_item.*
import kotlinx.coroutines.runBlocking

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var userLinksList = ArrayList<UserLinksModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setSupportActionBar(toolbar_edit_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_edit_profile.setNavigationOnClickListener {
            finish()
        }

        if(currentUser != null){
            edit_profile_user_picture.setImageBitmap(byteArrayToBitmap(currentUser!!.profile_picture.bytes))

            edit_profile_user_name_tv.text = currentUser!!.name
            edit_profile_user_name_et.setText(currentUser!!.name)

            edit_profile_user_surname_tv.text = currentUser!!.surname
            edit_profile_user_surname_et.setText(currentUser!!.surname)

            edit_profile_user_description_tv.text = currentUser!!.description
            edit_profile_user_description_et.setText(currentUser!!.description)

            edit_profile_user_email_tv.text = currentUser!!.email
            edit_profile_user_email_et.setText(currentUser!!.email)

            if(currentUser!!.background_picutre != null){
                edit_profile_bg_picture.setImageBitmap(byteArrayToBitmap(currentUser!!.background_picutre!!.bytes))
            }
        }

        readLinks()

        if(userLinksList.size > 0){
            linksRecyclerView(userLinksList)
        }

        user_name_edit_btn.setOnClickListener(this)
        user_name_edit_save_btn.setOnClickListener(this)
        user_surname_edit_btn.setOnClickListener(this)
        user_surname_edit_save_btn.setOnClickListener(this)
        user_email_edit_btn.setOnClickListener(this)
        user_email_edit_save_btn.setOnClickListener(this)
        user_description_edit_btn.setOnClickListener(this)
        user_description_edit_save_btn.setOnClickListener(this)


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

                if(edit_profile_user_name_tv.text.toString() != edit_profile_user_name_et.text.toString()){
                    val name = edit_profile_user_name_et.text.toString()
                    if(validateName(name)){
                        updateUserName(name)
                    }
                    edit_profile_user_name_tv.text = name
                }
            }
            R.id.user_surname_edit_btn -> {
                edit_profile_user_surname_tv.visibility = View.GONE
                edit_profile_user_surname_et.visibility = View.VISIBLE
                user_surname_edit_btn.visibility = View.GONE
                user_surname_edit_save_btn.visibility = View.VISIBLE
            }
            R.id.user_surname_edit_save_btn -> {
                edit_profile_user_surname_et.visibility = View.GONE
                edit_profile_user_surname_tv.visibility = View.VISIBLE
                user_surname_edit_save_btn.visibility = View.GONE
                user_surname_edit_btn.visibility = View.VISIBLE

                if(edit_profile_user_surname_tv.text.toString() != edit_profile_user_surname_et.text.toString()){
                    val surname = edit_profile_user_surname_et.text.toString()
                    if(validateSurname(surname)){
                        updateUserSurname(surname)
                    }
                    edit_profile_user_surname_tv.text = surname
                }
            }
            R.id.user_email_edit_btn -> {
                edit_profile_user_email_tv.visibility = View.GONE
                edit_profile_user_email_et.visibility = View.VISIBLE
                user_email_edit_btn.visibility = View.GONE
                user_email_edit_save_btn.visibility = View.VISIBLE
            }
            R.id.user_email_edit_save_btn -> {
                edit_profile_user_email_et.visibility = View.GONE
                edit_profile_user_email_tv.visibility = View.VISIBLE
                user_email_edit_save_btn.visibility = View.GONE
                user_email_edit_btn.visibility = View.VISIBLE

                if(edit_profile_user_email_tv.text.toString() != edit_profile_user_email_et.text.toString()){
                    val email = edit_profile_user_email_et.text.toString()
                    if(validateEmail(email)){
                        updateUserEmail(email)
                    }
                    edit_profile_user_email_tv.text = email
                }
            }
            R.id.user_description_edit_btn -> {
                edit_profile_user_description_tv.visibility = View.GONE
                edit_profile_user_description_et.visibility = View.VISIBLE
                user_description_edit_btn.visibility = View.GONE
                user_description_edit_save_btn.visibility = View.VISIBLE
            }
            R.id.user_description_edit_save_btn -> {
                edit_profile_user_description_et.visibility = View.GONE
                edit_profile_user_description_tv.visibility = View.VISIBLE
                user_description_edit_save_btn.visibility = View.GONE
                user_description_edit_btn.visibility = View.VISIBLE

                if(edit_profile_user_description_tv.text.toString() != edit_profile_user_description_et.text.toString()){
                    val description = edit_profile_user_description_et.text.toString()
                    if(validateDescription(description)){
                        updateUserDescription(description)
                    }
                    edit_profile_user_description_tv.text = description
                }
            }
        }
    }

    private fun linksRecyclerView(userLinks: ArrayList<UserLinksModel>){
        edit_profile_links_recycler_view.layoutManager = LinearLayoutManager(this)
        edit_profile_links_recycler_view.setHasFixedSize(true)
        val userLinks = UserLinksAdapter(this, userLinks)
        edit_profile_links_recycler_view.adapter = userLinks

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

    private fun readLinks() = runBlocking { // tmp method
        userLinksList.add(
            UserLinksModel("link1", "www.facebook.com")
        )
        userLinksList.add(
            UserLinksModel("link2", "www.youtube.com")
        )
    }
    private fun readUserLinks(){
        //TODO: WITOLD find user links => userLinksList: UserLinksModel
    }

    private fun validateName(s: String): Boolean{
        Log.e("valdiateName", s)
        return true
    }
    private fun updateUserName(s: String){ //TODO: Witold update user name
        Log.e("updateUserName", s)
    }
    private fun validateSurname(s: String): Boolean{
        Log.e("valdiateSurame", s)
        return true
    }
    private fun updateUserSurname(s: String){ //TODO: Witold update user surname
        Log.e("updateUserSurname", s)
    }
    private fun validateEmail(s: String): Boolean {
        Log.e("valdiateEmail", s)
        return true
    }
    private fun updateUserEmail(s: String){
        Log.e("updateUserEmail", s)
    }
    private fun validateDescription(s: String): Boolean {
        Log.e("validateDescription", s)
        return true
    }
    private fun updateUserDescription(s: String){
        Log.e("updateUserDescription", s)
    }
}