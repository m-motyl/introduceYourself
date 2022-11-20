package com.example.introduce_yourself.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.utils.byteArrayToBitmap
import com.example.introduce_yourself.utils.currentUser
import com.example.introduce_yourself.utils.saveImageByteArray
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.recyclerviewapp.UserLinksAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.runBlocking
import java.io.IOException

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var userLinksList = ArrayList<UserLinksModel>()
    private var backgroundByteArray: ByteArray = ByteArray(1)
    private var profilePictureByteArray: ByteArray = ByteArray(1)

    companion object {
        const val GALLERY_P_CODE = 1
        const val GALLERY_BG_CODE = 2
    }

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

//        readLinks()

        if(userLinksList.size > 0){
            linksRecyclerView(userLinksList)
        }else{
            edit_profile_no_links_tv.visibility = View.VISIBLE
            edit_profile_links_recycler_view.visibility = View.GONE
        }

        user_name_edit_btn.setOnClickListener(this)
        user_name_edit_save_btn.setOnClickListener(this)
        user_surname_edit_btn.setOnClickListener(this)
        user_surname_edit_save_btn.setOnClickListener(this)
        user_email_edit_btn.setOnClickListener(this)
        user_email_edit_save_btn.setOnClickListener(this)
        user_description_edit_btn.setOnClickListener(this)
        user_description_edit_save_btn.setOnClickListener(this)
        user_picture_edit_btn.setOnClickListener(this)
        user_bg_picture_edit_btn.setOnClickListener(this)
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
            R.id.user_picture_edit_btn -> {
                chooseFromGallery(GALLERY_P_CODE)
                if(!profilePictureByteArray.contentEquals(ByteArray(1))){
                    updateUserProfilePicture(profilePictureByteArray)
                }
            }
            R.id.user_bg_picture_edit_btn ->{
                chooseFromGallery(GALLERY_BG_CODE)
                if(!backgroundByteArray.contentEquals(ByteArray(1))){
                    updateUserBackgroundPicture(backgroundByteArray)
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

    private fun validateName(s: String): Boolean{ //TODO: Mateusz
        Log.e("valdiateName", s)
        return true
    }
    private fun updateUserName(s: String){ //TODO: Witold update user name
        Log.e("updateUserName", s)
    }
    private fun validateSurname(s: String): Boolean{ //TODO: Mateusz
        Log.e("valdiateSurame", s)
        return true
    }
    private fun updateUserSurname(s: String){ //TODO: Witold update user surname
        Log.e("updateUserSurname", s)
    }
    private fun validateEmail(s: String): Boolean { //TODO: Mateusz
        Log.e("valdiateEmail", s)
        return true
    }
    private fun updateUserEmail(s: String){ //TODO: Witold update
        Log.e("updateUserEmail", s)
    }
    private fun validateDescription(s: String): Boolean { //TODO: Mateusz
        Log.e("validateDescription", s)
        return true
    }
    private fun updateUserDescription(s: String){ //TODO: Witold update
        Log.e("updateUserDescription", s)
    }
    private fun updateUserProfilePicture(ba: ByteArray){ //TODO: Witold update
        Log.e("updateUserProfilePicture", "ok")
    }
    private fun updateUserBackgroundPicture(ba: ByteArray){ //TODO: Witold update
        Log.e("updateUserBackgroundPicture", "ok")
    }

    private fun chooseFromGallery(code: Int) {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(
                    report: MultiplePermissionsReport?
                ) {
                    if (report!!.areAllPermissionsGranted()) {
                        val galleryIntent =
                            Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                        startActivityForResult(
                            galleryIntent,
                            code
                        )
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    permissionDeniedDialog()
                }
            }).onSameThread().check()
    }

    private fun permissionDeniedDialog() {
        AlertDialog.Builder(this).setMessage("Brak uprawnień")
            .setPositiveButton("Przejdz do USTAWIEŃ")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package",
                        packageName,
                        null
                    )
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("ANULUJ")
            { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_P_CODE) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImage =
                            MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                contentURI
                            )
                        profilePictureByteArray = saveImageByteArray(selectedImage)
                        edit_profile_user_picture.setImageBitmap(selectedImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Błąd!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (requestCode == GALLERY_BG_CODE) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImage =
                            MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                contentURI
                            )
                        backgroundByteArray = saveImageByteArray(selectedImage)
                        edit_profile_bg_picture.setImageBitmap(selectedImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Błąd!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}