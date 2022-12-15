package com.example.introduce_yourself.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.introduce_yourself.Models.ReadInvitationsModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.utils.byteArrayToBitmap
import kotlinx.android.synthetic.main.community_invitation_item.view.*

open class UserInvitationsAdapter(
    private val context: Context,
    private var listOfUsers: ArrayList<ReadInvitationsModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private var onAcceptClickListener: OnAcceptClickListener? = null
    private var onRejectClickListener: OnRejectClickListener? = null

    private class OwnViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OwnViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.community_invitation_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ptr = listOfUsers[position]
        if (holder is OwnViewHolder) {
            holder.itemView.community_invitation_item_user_name.text = ptr.name + " " + ptr.surname
            holder.itemView.community_invitation_item_user_email.text = ptr.email
            holder.itemView.community_invitation_item_user_picture.setImageBitmap(byteArrayToBitmap(ptr.image))

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, ptr)
                }
            }
            holder.itemView.community_invitation_item_accept.setOnClickListener{
                if (onAcceptClickListener != null) {
                    onAcceptClickListener!!.onClick(position, ptr)
                }
            }
            holder.itemView.community_invitation_item_reject.setOnClickListener{
                if (onRejectClickListener != null) {
                    onRejectClickListener!!.onClick(position, ptr)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    fun setOnClickListener(
        onClickListener: OnClickListener,
        onAcceptClickListener: OnAcceptClickListener,
        onRejectClickListener: OnRejectClickListener
    ) {
        this.onClickListener = onClickListener
        this.onAcceptClickListener = onAcceptClickListener
        this.onRejectClickListener = onRejectClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: ReadInvitationsModel)
    }
    interface OnAcceptClickListener {
        fun onClick(position: Int, model: ReadInvitationsModel)
    }
    interface OnRejectClickListener {
        fun onClick(position: Int, model: ReadInvitationsModel)
    }
}