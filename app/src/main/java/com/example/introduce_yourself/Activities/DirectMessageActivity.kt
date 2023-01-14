package com.example.introduce_yourself.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

class DirectMessageActivity : AppCompatActivity(), View.OnClickListener {
    private var stalked_user: User? = null
    private var readUserModel: ReadUserModel? = null
    private var messagesList = ArrayList<MessageModel>()
    private val current = true
    private val other = false
    private var offset: Long = 0L
    private var end = false

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
//            readFullUser()
            supportActionBar!!.title = readUserModel!!.email
        }

        direct_message_send.setOnClickListener(this)

//        conversation() //delete if not necessary
        loadMessages(0)
        messagesRecyclerView(messagesList)
        direct_messages_list_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    Log.e("reached", "bottom")
                }

                if (!recyclerView.canScrollVertically(-1)) {
                    loadMessages(offset + 20)
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })

        direct_messages_list_rv.smoothScrollToPosition(messagesList.size - 1)
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
                            user = i.from != currentUser //todo mateusz it is backwards false == currentUser
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
                sendMessage(direct_message_text.text.toString())
                messagesList.add(
                    MessageModel(
                        direct_message_text.text.toString(),
                        current
                    )
                )
                direct_message_text.text.clear()
                messagesRecyclerView(messagesList)
                direct_messages_list_rv.smoothScrollToPosition(messagesList.size - 1)
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

    private fun conversation() {

        messagesList.add(
            MessageModel(
                "siema siema siema siema siema siema siema siema siema",
                current
            )
        )
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam", current))
        messagesList.add(MessageModel("hej", other))
        messagesList.add(MessageModel("co tam?", current))

        messagesRecyclerView(messagesList)
    }

    private fun messagesRecyclerView(messagesModelList: ArrayList<MessageModel>) {
        direct_messages_list_rv.layoutManager = LinearLayoutManager(this)
        direct_messages_list_rv.setHasFixedSize(true)
        val messagesList = DirectMessagesAdapter(this, messagesModelList)
        direct_messages_list_rv.adapter = messagesList

        messagesList.setOnClickListener(object : DirectMessagesAdapter.OnClickListener {
            override fun onClick(position: Int, model: MessageModel) {

            }
        })
    }
}