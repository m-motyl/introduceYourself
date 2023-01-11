package com.example.introduce_yourself.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.introduce_yourself.Models.MessageModel
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.R
import kotlinx.android.synthetic.main.message_item.view.*
import kotlinx.android.synthetic.main.user_item_links_item_row.view.*

open class DirectMessagesAdapter(
    private val context: Context,
    private var listOfUsers: ArrayList<MessageModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    private class OwnViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OwnViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.message_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ptr = listOfUsers[position]
        if (holder is OwnViewHolder) {

            if (ptr.user) { //current
                holder.itemView.message_item_other_user_card_view.visibility = View.GONE
                holder.itemView.message_item_current_user_card_view.visibility = View.VISIBLE

                holder.itemView.message_item_current_user_text.text = ptr.text
            } else { //other
                holder.itemView.message_item_other_user_card_view.visibility = View.VISIBLE
                holder.itemView.message_item_current_user_card_view.visibility = View.GONE

                holder.itemView.message_item_other_user_text.text = ptr.text
            }

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
        fun onClick(position: Int, model: MessageModel)
    }
}