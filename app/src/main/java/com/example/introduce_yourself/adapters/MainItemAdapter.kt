package com.recyclerviewapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import kotlinx.android.synthetic.main.main_item_row.view.*

open class UsersList(
    private val context: Context,
    private var listOfUsers: ArrayList<ReadUserModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    private class OwnViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OwnViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.main_item_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ptr = listOfUsers[position]
        if (holder is OwnViewHolder) {
            holder.itemView.main_tv_user_name.text = ptr.name + " " + ptr.surname
            holder.itemView.main_tv_user_email.text = ptr.email
            if(ptr.description != "") {
                holder.itemView.main_tv_user_description.visibility = View.VISIBLE
                holder.itemView.main_tv_user_description.text = ptr.description
            }
            else{
                holder.itemView.main_tv_user_description.visibility = View.GONE
            }
            holder.itemView.main_iv_user_picture.setImageBitmap(byteArrayToBitmap(ptr.profile_picture))
            if(ptr.ranking != 0) {
                holder.itemView.main_tv_user_likes.visibility = View.VISIBLE
                holder.itemView.main_tv_user_likes.text = "Polubienia: " + ptr.ranking.toString()
            }
            else {
                holder.itemView.main_tv_user_likes.visibility = View.GONE
            }
            //passing which position was clicked on rv
            //passing ptr
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, ptr)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: ReadUserModel)
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}