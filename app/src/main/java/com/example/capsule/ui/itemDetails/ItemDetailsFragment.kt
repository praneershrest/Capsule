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
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.R
import com.example.capsule.Util
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.ui.closet.ClosetFragment
import java.io.File

private const val IMG_URI_KEY = R.string.img_uri_key.toString()
private const val IMG_FILENAME_KEY = R.string.img_filename_key.toString()

class ItemDetailsFragment : Fragment() {
    private var imgUriString: String? = null
    private lateinit var imageView: ImageView

    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: ItemDetailsViewModelFactory
    private lateinit var itemDetailsViewModel: ItemDetailsViewModel

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
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = ItemDetailsViewModelFactory(databaseRepository)
        itemDetailsViewModel =
            ViewModelProvider(this, factory)[ItemDetailsViewModel::class.java]

        itemDetailsViewModel.allClothingEntriesLiveData.observe(requireActivity()){println("Test " + it)}

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
        var price = "0.00"
        if (!priceEditText.text.toString().isEmpty()) {
            price = String.format("%.2f", priceEditText.text.toString().toDouble())
        }
        var purchaseLocation = purchaseLocationSpinner.selectedItem.toString()
        var clothingEntry = Clothing(
            name = itemName,
            category = category,
            material = material,
            season = season,
            price = price.toDouble(),
            purchase_location = purchaseLocation
        )
        itemDetailsViewModel.insert(clothingEntry)
        val nextFrag: Fragment? = ClosetFragment()
        if (nextFrag != null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, nextFrag, R.string.fragment_key.toString())
                .addToBackStack(null)
                .commit()
        }
    }
}