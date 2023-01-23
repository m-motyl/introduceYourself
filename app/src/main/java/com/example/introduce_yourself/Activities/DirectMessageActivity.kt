package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.introduce_yourself.Models.MessageModel
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.Models.UserLinksModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.adapters.DirectMessagesAdapter
import com.example.introduce_yourself.database.*
import com.example.introduce_yourself.utils.currentUser
import com.recyclerviewapp.UsersList
import kotlinx.android.synthetic.main.activity_direct_message.*
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.message_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class DirectMessageActivity : AppCompatActivity(), View.OnClickListener {
    private var stalked_user: User? = null
    private var readUserModel: ReadUserModel? = null
    private var messagesList = ArrayList<MessageModel>()
    private val current = true
    private var loadMore = false
    private val other = false
    private var offset: Long = 0L
    private var end = false
    private var counter = 1

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
        setContentView(R.layout.activity_direct_message)

        setSupportActionBar(toolbar_direct_message)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_direct_message.setNavigationOnClickListener {
            finish()
        }

        if (intent.hasExtra(MessagesActivity.FRIEND_DETAILS)) {
            readUserModel = intent.getSerializableExtra(MessagesActivity.FRIEND_DETAILS)
                    as ReadUserModel
        }
        if (intent.hasExtra(UserItemActivity.FRIEND_DETAILS)) {
            readUserModel = intent.getSerializableExtra(UserItemActivity.FRIEND_DETAILS)
                    as ReadUserModel //>???
        }

        if (readUserModel != null) {
            supportActionBar!!.title = readUserModel!!.email
        }

        direct_message_send.setOnClickListener(this)

        loadMessages(0)
        if(end) {
            messagesRecyclerView(
                messagesTimeStamps(messagesList),
                loadMore=false
            )
        }else{
            messagesRecyclerView(
                messagesTimeStamps(messagesList)
            )
        }

        if(messagesList.size > 0) {
            direct_messages_list_rv.scrollToPosition(messagesList.size - 1)
        }
    }

    private fun loadMessages(offset: Long) {
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                val l = Message.find {
                    ((Messages.from eq currentUser!!.id) and (Messages.to eq readUserModel!!.id)) or
                            ((Messages.from eq readUserModel!!.id) and (Messages.to eq currentUser!!.id))
                }.limit(20, offset).orderBy(Messages.time to SortOrder.DESC).toList()
                if (l.size < 20)
                    end = true

                for (i in l) {
                    messagesList.add(
                        MessageModel(
                            text = i.content,
                            time = i.time,
                            user = i.from.id == currentUser!!.id
                        )
                    )
                }
            }
        }
        this.offset = offset
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.direct_message_send -> {
                when {
                    direct_message_text.length() > 500 -> {
                        Toast.makeText(
                            this,
                            "Wiadomość może zawierać maksymalnie 500 znaków!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    direct_message_text.text.trim().isEmpty() -> {
                        Toast.makeText(
                            this,
                            "Wiadomość nie może składać się z samych znaków białych!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        sendMessage(direct_message_text.text.toString())
                        direct_message_text.text.clear()
                        messagesList.clear()
                        loadMessages(0)
                        if(end) {
                            messagesRecyclerView(
                                messagesTimeStamps(messagesList),
                                loadMore=false
                            )
                        }else{
                            messagesRecyclerView(
                                messagesTimeStamps(messagesList)
                            )
                        }
                        direct_messages_list_rv.scrollToPosition(messagesList.size - 1)
                    }
                }
            }
        }
    }

    private fun sendMessage(msg: String) {
        runBlocking {
            newSuspendedTransaction(Dispatchers.IO) {
                Message.new {
                    content = msg
                    time = LocalDateTime.now()
                    from = currentUser!!
                    to = User.findById(readUserModel!!.id)!!
                }
            }
        }
    }

    private fun messagesRecyclerView(messagesModelList: ArrayList<MessageModel>, loadMore: Boolean=true) {
        direct_messages_list_rv.layoutManager = LinearLayoutManager(this)
        direct_messages_list_rv.setHasFixedSize(true)
        val messages = DirectMessagesAdapter(this, messagesModelList, loadMore)
        direct_messages_list_rv.adapter = messages

        messages.setOnClickListener(object : DirectMessagesAdapter.OnClickListener {
            override fun onClick(position: Int, model: MessageModel) {

            }
        },
        object: DirectMessagesAdapter.OnLoadMoreClickListener{
            override fun onClick(position: Int, model: MessageModel) {

                direct_messages_list_rv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val h1 = direct_messages_list_rv.measuredHeight

                loadMessages(offset + 20)
                if(end) {
                    messagesRecyclerView(
                        messagesTimeStamps(messagesList),
                        loadMore=false
                    )
                }else{
                    messagesRecyclerView(
                        messagesTimeStamps(messagesList)
                    )
                }

                direct_messages_list_rv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val h2 = direct_messages_list_rv.measuredHeight

                if(messagesList.size > 0) {
                    direct_messages_list_rv.scrollBy(0, h2 - h1)
                }
            }
        })
    }
    private fun messagesTimeStamps(messagesModelList: ArrayList<MessageModel>): ArrayList<MessageModel> {
        if (messagesModelList.size > 0) {
            val messagesList = ArrayList<MessageModel>()
            ArrayList(messagesModelList.reversed()).forEachIndexed { index, messageModel ->
                if(
                    index == 0 || //time stamp at the top of the page
                    timeDifference(messageModel.time, messagesList.last().time) || //when there is more than 15 min diff
                    dayDifference(messageModel.time, messagesList.last().time) //when there is a days diff
                ){
                    messagesList.add(
                        MessageModel(
                            " ",
                            messageModel.time,
                            true
                        )
                    )
                }
                messagesList.add(messageModel)
            }

            return messagesList
        } else {
            return ArrayList(messagesModelList.reversed())
        }
    }

    private fun timeDifference(date1: LocalDateTime, date2: LocalDateTime): Boolean {
        return ChronoUnit.MINUTES.between(date2, date1) > 15
    }

    private fun dayDifference(date1: LocalDateTime, date2: LocalDateTime): Boolean {
        return date2.dayOfYear != date1.dayOfYear
    }
}