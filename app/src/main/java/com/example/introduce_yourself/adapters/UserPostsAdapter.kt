package com.example.introduce_yourself.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.utils.byteArrayToBitmap
import kotlinx.android.synthetic.main.edit_profile_posts_item_row.view.*
import kotlinx.android.synthetic.main.user_item_posts_item_row.view.*
import java.time.format.DateTimeFormatter

open class UserPostsAdapter(
    private val context: Context,
    private var listOfUsers: ArrayList<UserPostModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private var onLikeClickListener: OnLikeClickListener? = null
    private var onDislikeClickListener: OnDislikeClickListener? = null

    private class OwnViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OwnViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.user_item_posts_item_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ptr = listOfUsers[position]
        if (holder is OwnViewHolder) {
            holder.itemView.user_item_post_title.text = ptr.post_title
            holder.itemView.user_item_post_text.text = ptr.post_content
            holder.itemView.user_item_post_date.text =
                ptr.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            holder.itemView.user_item_post_like_number.text = ptr.likes.toString()
            holder.itemView.user_item_post_dislike_number.text = ptr.dislikes.toString()

            if (ptr.image.contentEquals(ByteArray(1))) {
                holder.itemView.user_item_post_image.visibility = View.GONE
            } else {
                holder.itemView.user_item_post_image.setImageBitmap(byteArrayToBitmap(ptr.image))
            }

            if(userLikes(ptr)){
                holder.itemView.user_item_post_like_btn.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24)
            }else{
                holder.itemView.user_item_post_like_btn.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
            }

            if(userDislikes(ptr)){
                holder.itemView.user_item_post_dislike_btn.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
            }else{
                holder.itemView.user_item_post_dislike_btn.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_24)
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, ptr)
                }
            }
            holder.itemView.user_item_post_like_btn.setOnClickListener {
                if (onLikeClickListener != null) {
                    onLikeClickListener!!.onClick(position, ptr)
//                    Log.e("like", ptr.toString())
                }
            }
            holder.itemView.user_item_post_dislike_btn.setOnClickListener {
                if (onDislikeClickListener != null) {
                    onDislikeClickListener!!.onClick(position, ptr)
//                    Log.e("dislike", ptr.toString())
                }
            }
        }
    }

    private fun userLikes(ptr: UserPostModel): Boolean { //TODO WITOLD check if current user likes the post
        return true
    }

    private fun userDislikes(ptr: UserPostModel): Boolean { //TODO WITOLD check if current user dislikes the post
        return false
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    fun setOnClickListener(
        onClickListener: OnClickListener,
        onLikeClickListener: OnLikeClickListener,
        onDislikeClickListener: OnDislikeClickListener
    ) {
        this.onClickListener = onClickListener
        this.onLikeClickListener = onLikeClickListener
        this.onDislikeClickListener = onDislikeClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }

    interface OnLikeClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }

    interface OnDislikeClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }
}