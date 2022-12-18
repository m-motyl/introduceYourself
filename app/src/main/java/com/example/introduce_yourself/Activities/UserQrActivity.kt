package com.example.introduce_yourself.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.introduce_yourself.Models.ReadUserModel
import com.example.introduce_yourself.R
import com.example.introduce_yourself.database.User
import com.example.introduce_yourself.database.Users
import com.example.introduce_yourself.utils.currentUser
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_user_qr.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserQrActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var qrIV: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var qrEncoder: QRGEncoder
    private lateinit var codescanner: CodeScanner
    private var qrSwitch: Boolean = false
    private var userFound: ReadUserModel? = null

    companion object {
        const val QR_CODE = 1
        const val QR_USER_FOUND = "QR_USER_FOUND"
    }

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
        setContentView(R.layout.activity_user_qr)

        setSupportActionBar(toolbar_user_qr)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Skaner kodów QR"

        toolbar_user_qr.setNavigationOnClickListener {
            finish()
        }
        qrIV = findViewById(R.id.user_qr_display_qr)
        val display: Display = windowManager.defaultDisplay
        val point: Point = Point()
        display.getSize(point)

        val width = point.x
        val height = point.y

        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        qrEncoder = QRGEncoder(
            currentUser!!.email,
            null,
            QRGContents.Type.TEXT,
            dimen
        )

        try {
            bitmap = qrEncoder.encodeAsBitmap()
            qrIV.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        user_qr_switch.setOnClickListener(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                QR_CODE
            )
        } else {
            startScanning()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.user_qr_switch -> {
                if (user_qr_scanner.visibility == View.GONE) {
                    user_qr_scanner.visibility = View.VISIBLE
                    user_qr_display_qr.visibility = View.GONE
                    qrSwitch = true
                } else {
                    user_qr_scanner.visibility = View.GONE
                    user_qr_display_qr.visibility = View.VISIBLE
                    qrSwitch = false
                }
            }
        }
    }

    private fun startScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.user_qr_scanner)
        codescanner = CodeScanner(this, scannerView)
        codescanner.camera = CodeScanner.CAMERA_BACK
        codescanner.formats = CodeScanner.ALL_FORMATS

        codescanner.autoFocusMode = AutoFocusMode.SAFE
        codescanner.scanMode = ScanMode.SINGLE
        codescanner.isAutoFocusEnabled = true
        codescanner.isFlashEnabled = false

        codescanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (qrSwitch) {
                    Log.e("text", it.text.toString())
                    findUser(it.text)
                    if (userFound != null) {
                        val intent = Intent(
                            this@UserQrActivity,
                            UserItemActivity::class.java
                        )
                        intent.putExtra(
                            QR_USER_FOUND,
                            userFound
                        )
                        startActivity(intent)
                    }
                }
            }
        }
        codescanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Camera initialization error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codescanner.startPreview()
        }
    }

    private fun findUser(email: String) = runBlocking {
        newSuspendedTransaction(Dispatchers.IO) {
            val u = User.find { Users.email eq email }.toList()
            if (u.isNotEmpty())
                userFound = ReadUserModel(
                    id = u[0].id.value,
                    name = u[0].name,
                    surname = u[0].surname,
                    email = u[0].email,
                    description = u[0].description,
                    profile_picture = u[0].profile_picture.bytes,
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == QR_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codescanner.isInitialized) {
            codescanner?.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codescanner.isInitialized) {
            codescanner?.releaseResources()
        }
    }
}