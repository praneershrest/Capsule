package com.example.capsule.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
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

    /**
     * check access to external storage
     */
    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }

    /**
     * create an imageFile to save when a user chooses a picture from the gallery
     */
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

    /**
     * Object for season
     */
    object Season{
        const val SPRING = "Spring"
        const val SUMMER = "Summer"
        const val FALL = "Fall"
        const val WINTER = "Winter"
    }

    /**
     * Object for weather
     */
    object Weather{
        const val THUNDER = "Thunderstorm"
        const val DRIZZLE = "Drizzle"
        const val RAIN = "Rain"
        const val SNOW = "Snow"
        const val SUNNY = "Clear"
        const val CLOUDY = "Clouds"
    }

    /**
     * determine a season based on the given temperature
     */
    fun determineSeason(temp: Int) : String{
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

    /**
     * function to parse a calendar object to string in the given format
     */
    fun calendarToString(cal: Calendar) : String {
        val sdf: DateFormat = SimpleDateFormat("EEE, MMM d, yyyy")
        return sdf.format(cal.time)
    }

    /**
     * function to parse a milisecond time to a formatted date
     */
    fun millisToStringFormattedDate(millis: Long) : String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendarToString(calendar)
    }
}