package com.example.introduce_yourself.Activities

import android.graphics.Bitmap
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.introduce_yourself.R
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.example.introduce_yourself.utils.currentUser
import kotlinx.android.synthetic.main.activity_user_item.*
import kotlinx.android.synthetic.main.activity_user_item.toolbar_user_item
import kotlinx.android.synthetic.main.activity_user_qr.*

class UserQrActivity : AppCompatActivity() {

    lateinit var qrIV: ImageView
    lateinit var bitmap: Bitmap
    lateinit var qrEncoder: QRGEncoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_qr)

        setSupportActionBar(toolbar_user_qr)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Skaner kodów QR"

        toolbar_user_qr.setNavigationOnClickListener {
            finish()
        }
        qrIV = findViewById(R.id.idIVQrcode)
        val display: Display = windowManager.defaultDisplay
        val point: Point = Point()
        display.getSize(point)

        val width = point.x
        val height = point.y

        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        qrEncoder = QRGEncoder(currentUser!!.email, null, QRGContents.Type.TEXT, dimen)

        try {
            bitmap = qrEncoder.encodeAsBitmap()
            qrIV.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}