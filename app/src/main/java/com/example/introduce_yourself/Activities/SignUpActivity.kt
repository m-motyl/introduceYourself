package com.example.introduce_yourself.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.example.introduce_yourself.Models.SignInModel
import com.example.introduce_yourself.Models.SignUpModel
import com.example.introduce_yourself.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var isImage: Boolean = false
    private lateinit var saveImageByteArray: ByteArray
    private var signUpModel: SignUpModel? = null

    companion object {
        const val GALLERY_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        su_tv_signin.setOnClickListener(this)
        su_btn_signup.setOnClickListener(this)
        su_iv_avatar.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.su_tv_signin -> {
                finish()
            }
            R.id.su_btn_signup -> {
                when {
                    su_et_name.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj imię!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_name.length() > 30 || su_et_name.length() < 2 -> {
                        Toast.makeText(
                            this,
                            "Imię powinno zawierać od 2 do 30 liter!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_surname.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj nazwisko!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_surname.length() > 30 || su_et_surname.length() < 2 -> {
                        Toast.makeText(
                            this,
                            "Nazwisko powinno zawierać od 2 do 30 liter!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_email.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj e-mail!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_email.length() > 50 || su_et_email.length() < 5 -> {
                        Toast.makeText(
                            this,
                            "E-mail powinien zawierać od 5 do 50 liter!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    !isEmailValid(su_et_email.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Nieprawidłowy format e-mail!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    checkIfUserExists(su_et_email.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Użytkownik o podanym e-mailu istnieje!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_password.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj hasło!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    su_et_password.length() > 20 || su_et_password.length() < 2 -> {
                        Toast.makeText(
                            this,
                            "Hasło powinno zawierać od 2 do 20 znaków",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    !isImage -> {
                        Toast.makeText(
                            this,
                            "Dodaj zdjęcie profilowe!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        signUpModel = SignUpModel(
                            su_et_name.text.toString(),
                            su_et_surname.text.toString(),
                            su_et_email.text.toString(),
                            su_et_password.text.toString(),
                            saveImageByteArray
                        )
                        registerUser(signUpModel!!)

                        finish()
                    }
                }
            }
            R.id.su_iv_avatar -> {
                chooseFromGallery()
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkIfUserExists(email: String): Boolean { //TODO: WITOLD check if user exists
        return false
    }

    private fun registerUser(sum: SignUpModel){ //TODO: WITOLD register user
        Toast.makeText(
            this,
            "Konto utworzone pomyślnie!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun chooseFromGallery() {
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
                            GALLERY_CODE
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
                        null)
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
            if (requestCode == GALLERY_CODE) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImage =
                            MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                contentURI
                            )
                        saveImageByteArray = saveImageByteArray(selectedImage)
                        isImage = true
                        su_iv_avatar.setImageBitmap(selectedImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@SignUpActivity,
                            "Błąd!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun saveImageByteArray(
        bitmap: Bitmap
    ): ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            stream
        )
        return stream.toByteArray()
    }
}