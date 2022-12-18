package com.example.introduce_yourself.Activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.utils.currentUser
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_community.toolbar_community
import kotlinx.android.synthetic.main.activity_settings.*
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
        when(v!!.id){
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
}