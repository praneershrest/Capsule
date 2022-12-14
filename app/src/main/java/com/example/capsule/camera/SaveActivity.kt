package com.example.capsule.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.capsule.R

/**
 * Activity to allow users to see a preview of the picture before
 * they decide to save it or choose to retake
 */
class SaveActivity : AppCompatActivity() {
    private lateinit var newPictureURI: Uri
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var retakeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)
        imageView = findViewById(R.id.image_preview)
        saveButton = findViewById(R.id.picture_save_button)
        retakeButton = findViewById(R.id.picture_retake_button)
        newPictureURI = intent.getParcelableExtra("uri")!!
        imageView.setImageURI(newPictureURI)
        supportActionBar?.hide()

        saveButton.setOnClickListener{save()}
        retakeButton.setOnClickListener{cancel()}

    }

    /**
     * Save the picture by sending a flag back to the cameraActivity to save
     */
    fun save(){
        val intent = Intent()
        intent.putExtra("return_flag", "Save")
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * Cancel saving the picture by sending a flag back to the cameraActivity to cancel
     */

    fun cancel(){
        val intent = Intent()
        intent.putExtra("return_flag", "Cancel")
        setResult(RESULT_OK, intent)
        finish()
    }


}