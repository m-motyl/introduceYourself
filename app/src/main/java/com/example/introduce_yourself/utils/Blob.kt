package com.example.introduce_yourself.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun byteArrayToBitmap(data: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(data, 0, data.size)
}

fun saveImageByteArray(
    bitmap: Bitmap
): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(
        Bitmap.CompressFormat.JPEG,
        100,
        stream
    )
    return stream.toByteArray()
}