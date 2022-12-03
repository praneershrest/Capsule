package com.example.capsule

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
        }
    }

    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @Throws(IOException::class)
    fun createImageFile(currActivity: Activity): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = currActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    object Season{
        const val SPRING = "Spring"
        const val SUMMER = "Summer"
        const val FALL = "Fall"
        const val WINTER = "Winter"
    }

    fun determineSeason(temp: Double) : String{
        val month = Calendar.getInstance().get(Calendar.MONTH)+1
        if(temp < 5 ){
            return Season.WINTER
        }
        else if (temp > 5 && temp < 20 && month > 3 && month < 7){
            return Season.SPRING
        }
        else if(temp > 20){
            return Season.SUMMER
        }
        else if (temp > 5 && temp < 20 && month > 8 && month < 12){
            return Season.FALL
        }
        else if (temp > 5 && temp < 20){
            return Season.FALL
        }
        else{
            return Season.SPRING
        }
    }

    fun calendarToString(cal: Calendar) : String {
        val sdf: DateFormat = SimpleDateFormat("EEE, MMM d, yyyy")
        return sdf.format(cal.time)
    }
}