package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.introduce_yourself.Models.SignInModel
import com.example.introduce_yourself.R
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private var signInModel: SignInModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        si_btn_signin.setOnClickListener(this)
        si_tv_signup.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.si_btn_signin -> {
                when {
                    si_et_login.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj login!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    si_et_password.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj hasło!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        signInModel = SignInModel(
                            email = si_et_login.text.toString(),
                            password = si_et_password.text.toString()
                        )

                        if (validateUser(signInModel!!)) {
                            val intent = Intent(
                                this,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(
                                this,
                                "Nieprawidłowy e-mail lub hasło!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            R.id.si_tv_signup -> {
                val intent = Intent(
                    this,
                    SignUpActivity::class.java
                )
                startActivity(intent)
            }
        }
    }

    private fun validateUser(sim: SignInModel): Boolean{ //TODO WITOLD validate user
        return true
    }
}