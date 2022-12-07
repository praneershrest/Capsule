package com.example.capsule.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capsule.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), ImageCapture.OnImageSavedCallback {
    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var saveActivityResult: ActivityResultLauncher<Intent>

    private lateinit var currImageUri: Uri

    private lateinit var currImageFile: File


    /**
     *function to initiate the camera, setup the capture button and also the result from the
     * next activity when after the capture button is pressed for the preview
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.hide()

        // Request camera permissions
        if (allPermissionsGranted()) {
            Log.v(TAG, "onCreate all permissions granted")
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        saveActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val returnFlag = result.data?.getStringExtra("return_flag")
                println("DEBUG capsule -> $returnFlag")
                when(result.data?.getStringExtra("return_flag")){
                    "Cancel" -> {
                        deleteCurrImage()
                    }
                    "Save" -> {
                        save()
                    }
                }
            }
        }
    }

    /**
     * delete's the current image if the user decides to take retake the picture
     */
    private fun deleteCurrImage(){
        currImageFile.delete()
    }

    /**
     * saves the current image if the user decides to save the picture
     */
    private fun save(){
        val intent = Intent()
        intent.putExtra("uri", currImageUri)
        intent.putExtra("file", currImageFile)
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * function which actually takes the image and saves it into a new file saved in external files.
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        currImageFile = File(getExternalFilesDir(null), System.currentTimeMillis().toString() + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(currImageFile)
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),this)
    }

    /**
     * when the attempt to take an image of the camera is interrupted
     */

    override fun onError(exc: ImageCaptureException) {
        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
    }

    /**
     * when the image is saved, will start a new intent to the save activity to let the user decide to save or retake a picture
     */

    override fun
            onImageSaved(output: ImageCapture.OutputFileResults){
        val msg = "Photo capture succeeded: ${output.savedUri}"
        val intent = Intent(this, SaveActivity::class.java)
        intent.putExtra("uri", output.savedUri)
        saveActivityResult.launch(intent)
        currImageUri = output.savedUri!!
        Log.d(TAG, msg)
    }


    /**
     * initialises the camera/viewfinder so the use can see the viewfinder live.
     * adapted code from: https://developer.android.com/codelabs/camerax-getting-started#4
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    /**
     * when all permissions are granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * right after popup/ dialog box to request permission is finished this is called
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Log.v(TAG, "onRequestPermissionResult all permissions granted")
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * on activity destroy
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /**
     * values required to be initialised for the activity
     */
    companion object {
        private const val TAG = "DEBUG Capsule ->"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}