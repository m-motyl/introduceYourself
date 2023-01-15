package com.example.introduce_yourself.Activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import hashString
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_community.toolbar_community
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val THEME_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        when (currentUser!!.color_nr) {
            0 -> {
                setTheme(R.style.Theme0_Introduce_yourself)
            }
            1 -> {
                setTheme(R.style.Theme1_Introduce_yourself)
            }
            2 -> {
                setTheme(R.style.Theme2_Introduce_yourself)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar_settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_settings.setNavigationOnClickListener {
//            setResult(Activity.RESULT_OK)
            finish()
        }
        set_theme0_btn.setOnClickListener(this)
        set_theme1_btn.setOnClickListener(this)
        set_theme2_btn.setOnClickListener(this)
        settings_update_password_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.set_theme0_btn -> {
                updateUserColor(0)
                reloadActivity()
            }
            R.id.set_theme1_btn -> {
                updateUserColor(1)
                reloadActivity()
            }
            R.id.set_theme2_btn -> {
                updateUserColor(2)
                reloadActivity()
            }
            R.id.settings_update_password_btn -> {
                when {
                    !checkIfCorrectPassword(settings_old_password_et.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Stare hasło jest nieprawidłowe!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("oldPassword", "incorrect")
                    }
                    settings_new_password_et1.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Podaj nowe hasło!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    settings_new_password_et2.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Powtórz nowe hasło!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    settings_new_password_et1.length() > 20 || settings_new_password_et1.length() < 2 -> {
                        Toast.makeText(
                            this,
                            "Hasło powinno zawierać od 2 do 20 znaków",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    !isPasswordValid(settings_new_password_et1.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Hasło powinno zawierać jeden znak specjalny, jedną wielką literę" +
                                    " oraz nie posiadać białych znaków!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    (settings_new_password_et1.text.toString() != settings_new_password_et2.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Podane hasła nie są takie same!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    (settings_old_password_et.text.toString() == settings_new_password_et1.text.toString()) -> {
                        Toast.makeText(
                            this,
                            "Nowe hasło musi różnić się od poprzedniego!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        updatePassword(settings_new_password_et1.text.toString())

                        settings_old_password_et.text.clear()
                        settings_new_password_et1.text.clear()
                        settings_new_password_et2.text.clear()

                        Toast.makeText(
                            this,
                            "Hasło zostało zaktualizowane!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun updatePassword(newPassword: String) = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            User.findById(currentUser!!.id)!!.password = hashString(newPassword)
        }
    }

    private fun checkIfCorrectPassword(oldPassword: String): Boolean {
        return runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                User.findById(currentUser!!.id)!!.password == hashString(
                    oldPassword
                )
            }
        }
    }

    private fun updateUserColor(n: Int) = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            User.findById(currentUser!!.id)!!.color_nr = n
        }
    }

    private fun refreshCurrentUser() = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            currentUser = User.findById(currentUser!!.id)
        }
    }

    private fun reloadActivity() {
        refreshCurrentUser()
        finish()
        startActivityForResult(getIntent(), THEME_CODE)
    }

    private fun isPasswordValid(password: String): Boolean {
        val regex = ("^" +
                "(?=.*[!@#$%^&+=])" +    // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                "(?=.*[A-Z])" +          // at least 1 capital letter
                "").toRegex()
        return regex.containsMatchIn(password)
    }
}