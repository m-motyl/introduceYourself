package com.example.introduce_yourself.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introduce_yourself.R
import com.recyclerviewapp.ItemAdapter
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the LayoutManager that this RecyclerView will use.
        main_recycler_view.layoutManager = LinearLayoutManager(this)

        // Adapter class is initialized and list is passed in the param.
        val itemAdapter = UsersList(this, getUsersList())

        // adapter instance is set to the recyclerview to inflate the items.
        main_recycler_view.adapter = itemAdapter
    }

    private fun getUsersList(): Any {

    }//TODO: Mateusz
}