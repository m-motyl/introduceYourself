package com.example.introduce_yourself.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.introduce_yourself.Activities.EditProfileActivity
import com.example.introduce_yourself.Models.UserPostModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.UserPost
import com.example.introduce_yourself.utils.byteArrayToBitmap
import kotlinx.android.synthetic.main.edit_profile_posts_item_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class UserEditPostsAdapter(
    private val context: Context,
    private var listOfUsers: ArrayList<UserPostModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onEditClickListener: OnEditClickListener? = null
    private var onEditSaveClickListener: OnEditSaveClickListener? = null
    private var onEditAbortClickListener: OnEditAbortClickListener? = null
//    private var onEditImageClickListener: OnEditImageClickListener? = null

    private class OwnViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OwnViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.edit_profile_posts_item_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ptr = listOfUsers[position]
        if (holder is OwnViewHolder) {
            holder.itemView.edit_profile_post_title_tv.text = ptr.post_title
            holder.itemView.edit_profile_post_text_tv.text = ptr.post_content
            holder.itemView.edit_profile_post_date.text =
                ptr.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            holder.itemView.edit_profile_post_like_number.text = ptr.likes.toString()
            holder.itemView.edit_profile_post_dislike_number.text = ptr.dislikes.toString()

            if(ptr.image.contentEquals(ByteArray(1))){
                holder.itemView.edit_profile_post_image.visibility = View.GONE
//                holder.itemView.post_image_edit_btn.visibility = View.GONE
            }else{
                holder.itemView.edit_profile_post_image.setImageBitmap(byteArrayToBitmap(ptr.image))
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, ptr)
                }
            }
            holder.itemView.post_delete_btn.setOnClickListener{
                if (onDeleteClickListener != null){
                    onDeleteClickListener!!.onClick(position, ptr)
                }
            }
            holder.itemView.post_edit_btn.setOnClickListener{
                if (onEditClickListener != null){
                    onEditClickListener!!.onClick(position, ptr)

                    holder.itemView.post_edit_btn.visibility = View.GONE
                    holder.itemView.post_delete_btn.visibility = View.GONE

                    holder.itemView.post_edit_save_btn.visibility = View.VISIBLE
                    holder.itemView.post_edit_abort_btn.visibility = View.VISIBLE

                    //edit title
                    holder.itemView.edit_profile_post_title_tv.visibility = View.GONE
                    holder.itemView.edit_profile_post_title_et.visibility = View.VISIBLE
                    holder.itemView.edit_profile_post_title_et.setText(ptr.post_title)

                    //edit description
                    holder.itemView.edit_profile_post_text_tv.visibility = View.GONE
                    holder.itemView.edit_profile_post_text_et.visibility = View.VISIBLE
                    holder.itemView.edit_profile_post_text_et.setText(ptr.post_content)

                    //show edit photo button
//                    if(!ptr.image.contentEquals(ByteArray(1))) {
//                        holder.itemView.post_image_edit_btn.visibility = View.VISIBLE
//                    }

                }
            }
            holder.itemView.post_edit_save_btn.setOnClickListener{
                if (onEditSaveClickListener != null){
                    onEditSaveClickListener!!.onClick(position, ptr)
                    when {
                        ((holder.itemView.edit_profile_post_title_et.text.toString() == ptr.post_title) and
                            (holder.itemView.edit_profile_post_text_et.text.toString() == ptr.post_content)) -> {}

                        holder.itemView.edit_profile_post_title_et.text.toString().length < 5 ||
                                holder.itemView.edit_profile_post_title_et.text.toString().length > 50 -> {}

                        holder.itemView.edit_profile_post_text_et.text.toString().length < 5 ||
                                holder.itemView.edit_profile_post_text_et.text.toString().length > 300 -> {}

                        holder.itemView.edit_profile_post_title_et.text.toString().isNullOrEmpty() -> {}
                        holder.itemView.edit_profile_post_text_et.text.toString().isNullOrEmpty() -> {}

                        else -> {
                            editPost(
                                ptr,
                                holder.itemView.edit_profile_post_title_et.text.toString(),
                                holder.itemView.edit_profile_post_text_et.text.toString(),
                                LocalDateTime.now()
                            )
                            ptr.post_title = holder.itemView.edit_profile_post_title_et.text.toString()
                            ptr.post_content = holder.itemView.edit_profile_post_text_et.text.toString()
                            ptr.date = LocalDateTime.now()

                            //new title
                            holder.itemView.edit_profile_post_title_tv.setText(
                                holder.itemView.edit_profile_post_title_et.text.toString()
                            )

                            //new context
                            holder.itemView.edit_profile_post_text_tv.setText(
                                holder.itemView.edit_profile_post_text_et.text.toString()
                            )
//                            //new date
                            holder.itemView.edit_profile_post_date.text = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

                            holder.itemView.post_edit_btn.visibility = View.VISIBLE
                            holder.itemView.post_delete_btn.visibility = View.VISIBLE

                            holder.itemView.post_edit_save_btn.visibility = View.GONE
                            holder.itemView.post_edit_abort_btn.visibility = View.GONE

                            //save edit title
                            holder.itemView.edit_profile_post_title_tv.visibility = View.VISIBLE
                            holder.itemView.edit_profile_post_title_et.visibility = View.GONE

                            //save edit post
                            holder.itemView.edit_profile_post_text_tv.visibility = View.VISIBLE
                            holder.itemView.edit_profile_post_text_et.visibility = View.GONE

                            //save edit image
//                          holder.itemView.post_image_edit_btn.visibility = View.GONE
                        }
                    }
                }
            }
            holder.itemView.post_edit_abort_btn.setOnClickListener{
                if (onEditAbortClickListener != null){
                    onEditAbortClickListener!!.onClick(position, ptr)

                    holder.itemView.post_edit_btn.visibility = View.VISIBLE
                    holder.itemView.post_delete_btn.visibility = View.VISIBLE

                    holder.itemView.post_edit_save_btn.visibility = View.GONE
                    holder.itemView.post_edit_abort_btn.visibility = View.GONE

                    //abort edit title
                    holder.itemView.edit_profile_post_title_tv.visibility = View.VISIBLE
                    holder.itemView.edit_profile_post_title_et.visibility = View.GONE

                    //abort edit post
                    holder.itemView.edit_profile_post_text_tv.visibility = View.VISIBLE
                    holder.itemView.edit_profile_post_text_et.visibility = View.GONE

                    //abort edit image
//                    holder.itemView.post_image_edit_btn.visibility = View.GONE
                }
            }
//            holder.itemView.post_image_edit_btn.setOnClickListener{
//                if (onEditImageClickListener != null) {
//                    onEditImageClickListener!!.onClick(position, ptr)
//                }
//            }
        }
    }

    private fun editPost(
        oldPost: UserPostModel,
        newPostTitle: String,
        newPostContext: String,
        newDate: LocalDateTime
    ) = runBlocking{
        newSuspendedTransaction(Dispatchers.IO) {
            val p = UserPost.findById(oldPost.id!!)
            p!!.title = newPostTitle
            p.content = newPostContext
            p.date = newDate
        }
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    fun setOnClickListener(
        onClickListener: OnClickListener,
        onDeleteClickListener: OnDeleteClickListener,
        onEditClickListener: OnEditClickListener,
        onEditSaveClickListener: OnEditSaveClickListener,
        onEditAbortClickListener: OnEditAbortClickListener,
//        onEditImageClickListener: OnEditImageClickListener
    ) {
            this.onClickListener = onClickListener
            this.onDeleteClickListener = onDeleteClickListener
            this.onEditClickListener = onEditClickListener
            this.onEditSaveClickListener = onEditSaveClickListener
            this.onEditAbortClickListener = onEditAbortClickListener
//            this.onEditImageClickListener = onEditImageClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }
    interface OnDeleteClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }
    interface OnEditClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }
    interface OnEditSaveClickListener {
        fun onClick(position: Int, model: UserPostModel)
    }
    interface OnEditAbortClickListener{
        fun onClick(position: Int, model: UserPostModel)
    }
//    interface OnEditImageClickListener{
//        fun onClick(position: Int, model: UserPostModel)
//    }
}