package com.example.introduce_yourself.Activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.SignInModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var readUserModel = ArrayList<ReadUserModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUsersList()

        // Set the LayoutManager that this RecyclerView will use.
        main_recycler_view.layoutManager = LinearLayoutManager(this)

        // Adapter class is initialized and list is passed in the param.
        val itemAdapter = UsersList(this, readUserModel)

        // adapter instance is set to the recyclerview to inflate the items.
        main_recycler_view.adapter = itemAdapter
    }

    private fun getUsersList() {
        currentUser?.profile_picture?.let { ReadUserModel("mateusz", "motyl", it.bytes) }
            ?.let { readUserModel.add(it) } //test
    }//TODO: Witold wczytaj 5 userów i przypisz do readUserModel
}