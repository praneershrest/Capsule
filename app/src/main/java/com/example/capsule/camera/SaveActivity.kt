package com.example.capsule.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.capsule.R

class SaveActivity : AppCompatActivity() {
    private lateinit var newPictureURI: Uri
    private lateinit var imageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)
        imageView = findViewById(R.id.image_preview)
        saveButton = findViewById(R.id.picture_save_button)
        cancelButton = findViewById(R.id.picture_cancel_button)
        newPictureURI = intent.getParcelableExtra("uri")!!
        imageView.setImageURI(newPictureURI)

        saveButton.setOnClickListener{save()}
        cancelButton.setOnClickListener{cancel()}

    }

    fun save(){
        val intent = Intent()
        intent.putExtra("return_flag", "Save")
        setResult(RESULT_OK, intent)
        finish()
    }

    fun cancel(){
        val intent = Intent()
        intent.putExtra("return_flag", "Cancel")
        setResult(RESULT_OK, intent)
        finish()
    }


}