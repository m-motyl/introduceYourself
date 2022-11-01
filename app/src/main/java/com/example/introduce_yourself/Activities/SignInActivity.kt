package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.introduce_yourself.Models.SignInModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private var signInModel: SignInModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        si_btn_signin.setOnClickListener(this)
        si_tv_signup.setOnClickListener(this)

        connectToDb()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.si_btn_signin -> {
                when {
                    si_et_email.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj e-mail!",
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
                    !isEmailValid(si_et_email.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Niepoprawny format e-mail!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        signInModel = SignInModel(
                            email = si_et_email.text.toString(),
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

    private fun connectToDb(){
        runBlocking {
            Database.connect(
                "jdbc:postgresql://10.0.2.2:5432/iydb", driver = "org.postgresql.Driver",
                user = "postgres", password = "123"
            )
            newSuspendedTransaction(Dispatchers.IO) {
                SchemaUtils.create(Users)
                SchemaUtils.create(Cities)
                SchemaUtils.create(Friends)
                SchemaUtils.create(DailyLikes)
                SchemaUtils.create(Messages)
                SchemaUtils.create(UserLikes)
                SchemaUtils.create(UserLinks)
                SchemaUtils.create(UserPosts)
            }
        }
    }

    private fun validateUser(sim: SignInModel): Boolean{ //TODO WITOLD validate user
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}