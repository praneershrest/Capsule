package com.example.capsule.ui.itemDetails

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.example.capsule.R
import com.example.capsule.Util
import java.io.File

private const val IMG_URI_KEY = R.string.img_uri_key.toString()
private const val IMG_FILENAME_KEY = R.string.img_filename_key.toString()

class ItemDetailsFragment : Fragment() {
    private var imgUriString: String? = null
    private lateinit var imageView: ImageView

    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText

    private lateinit var categorySpinner: Spinner
    private lateinit var materialSpinner: Spinner
    private lateinit var seasonSpinner: Spinner
    private lateinit var purchaseLocationSpinner: Spinner
    private lateinit var imgFile: File
    private lateinit var imgUri: Uri
    private var saved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imgUriString = it.getString(IMG_URI_KEY)
            imgFile = it.getSerializable(IMG_FILENAME_KEY) as File
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = view.findViewById(R.id.current_pic_view)
        imgUri = Uri.parse(imgUriString)
        val bitmap = Util.getBitmap(requireActivity(), imgUri)
        imageView.setImageBitmap(bitmap)

        nameEditText = view.findViewById(R.id.itemName)
        priceEditText = view.findViewById(R.id.price)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        materialSpinner = view.findViewById(R.id.materialSpinner)
        seasonSpinner = view.findViewById(R.id.seasonSpinner)
        purchaseLocationSpinner = view.findViewById(R.id.purchaseSpinner)
        var onSaveEntryBtn: Button = view.findViewById(R.id.submitNewItem)
        onSaveEntryBtn.setOnClickListener {
            onSaveEntry()
        }
        createAdaptors()
    }

    fun createAdaptors() {
        var categoryAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.category_items,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var materialAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.material_items,
            android.R.layout.simple_spinner_item
        )
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var seasonAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.season_items,
            android.R.layout.simple_spinner_item
        )
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var purchaseLocationAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.purchase_items,
            android.R.layout.simple_spinner_item
        )
        purchaseLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        categorySpinner.adapter = categoryAdapter
        materialSpinner.adapter = materialAdapter
        seasonSpinner.adapter = seasonAdapter
        purchaseLocationSpinner.adapter = purchaseLocationAdapter
    }


    override fun onDestroy() {
        if (!saved){
            if (imgFile != null) {
                imgFile.delete()
            }
        }
        super.onDestroy()
    }

    private fun onSaveEntry(){
        saved = true

        var itemName = nameEditText.text.toString()
        var category = categorySpinner.selectedItem.toString()
        var material = materialSpinner.selectedItem.toString()
        var season = seasonSpinner.selectedItem.toString()
        var price = priceEditText.text.toString()
        var purchaseLocation = purchaseLocationSpinner.selectedItem.toString()

    }
}