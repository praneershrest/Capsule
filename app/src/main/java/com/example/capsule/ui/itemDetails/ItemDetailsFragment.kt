package com.example.capsule.ui.itemDetails

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.capsule.MainActivity.Companion.menuItems
import com.example.capsule.R
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.model.Clothing
import com.example.capsule.ui.closet.ClosetFragment
import com.example.capsule.utils.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView
import java.io.File


private const val IMG_URI_KEY = R.string.img_uri_key.toString()
private const val IMG_FILENAME_KEY = R.string.img_filename_key.toString()

/**
 * ItemDetails Fragment for holding the form where users can edit their clothing items
 */
class ItemDetailsFragment : Fragment() {

    // initialize the variables
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var imgUriString: String
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
    private lateinit var root: View
    private lateinit var navController: NavController
    private var saved = false
    private var renderPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        // get passed in argument from closet fragment and the camera and save argument values
        arguments?.let {
            if (it.getSerializable(IMG_FILENAME_KEY) != null){
                imgUriString = it.getString(IMG_URI_KEY).toString()
                imgFile = it.getSerializable(IMG_FILENAME_KEY) as File
            }
            else {
                renderPage = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // initialize database
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = ItemDetailsViewModelFactory(databaseRepository)
        itemDetailsViewModel =
            ViewModelProvider(this, factory)[ItemDetailsViewModel::class.java]

        // empty observer to allow using viewmodels to insert into database
        itemDetailsViewModel.allClothingEntriesLiveData.observe(requireActivity()){println("test " + it)}
        root = inflater.inflate(R.layout.fragment_item_details, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = root.findNavController()
        // on back call, if its this page, return to closet if there is no image
        if (!renderPage){
            navController.popBackStack()
            return
        }

        // initialize UI components
        imageView = view.findViewById(R.id.current_pic_view)
        imgUri = Uri.parse(imgUriString)
        imageView.setImageURI(imgUri)

        nameEditText = view.findViewById(R.id.itemName)
        priceEditText = view.findViewById(R.id.price)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        materialSpinner = view.findViewById(R.id.materialSpinner)
        seasonSpinner = view.findViewById(R.id.seasonSpinner)
        purchaseLocationSpinner = view.findViewById(R.id.purchaseSpinner)

        // save button will submit information to database on submit
        val onSaveEntryBtn: Button = view.findViewById(R.id.submitNewItem)
        onSaveEntryBtn.setOnClickListener {
            if (formIsValid()) {
                createDialog()
            } else {
                if (requireActivity().applicationContext != null) {
                    Toast.makeText(requireActivity().applicationContext, getString(R.string.toast_msg_fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            }
        }
        createAdaptors()

    }

    // creates a confirmation dialog for if the user wants to save the info or not
    private fun createDialog(){

        // initialize ui components in the dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val customLayout: View = layoutInflater.inflate(R.layout.fragment_confirmation_dialog, null)
        var textView: TextView = customLayout.findViewById(R.id.dialogText)
        var submitBtn: Button = customLayout.findViewById(R.id.dialog_submit_btn)
        var cancelBtn: Button = customLayout.findViewById(R.id.dialog_cancel_btn)
        submitBtn.text = getString(R.string.yes_btn_msg)
        cancelBtn.text = getString(R.string.cancel_btn_msg)
        textView.text = getString(R.string.confirm_upload_prompt)
        builder.setView(customLayout)

        // if the user clicks submit, it will save info to database
        val dialog: AlertDialog = builder.create()
        submitBtn.setOnClickListener{
            dialog.dismiss()
            Toast.makeText(requireActivity().applicationContext, getString(R.string.toast_msg_saved), Toast.LENGTH_SHORT).show()
            onSaveEntry()
        }
        // cancel closes dialog
        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }
        val window: Window? = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.show()
    }

    // creates all of adapters for all the drop down ui components
    private fun createAdaptors() {
        val categoryAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.category_items,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val materialAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.material_items,
            android.R.layout.simple_spinner_item
        )
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val seasonAdapter = ArrayAdapter.createFromResource(
            requireActivity().baseContext,
            R.array.season_items,
            android.R.layout.simple_spinner_item
        )
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val purchaseLocationAdapter = ArrayAdapter.createFromResource(
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
        arguments?.clear()
        arguments = null
        if (!saved){
            if (::imgFile.isInitialized && imgFile != null) {
                imgFile.delete()
            }
        }
        super.onDestroy()
    }

    // checks if the user input name and price
    private fun formIsValid(): Boolean {
        if (nameEditText.text.isNotEmpty() && priceEditText.text.isNotEmpty()){
            return true
        }
        return false
    }

    private fun onSaveEntry(){
        saved = true

        // get ui components to get the values stored in input
        val itemName = nameEditText.text.toString()
        val category = categorySpinner.selectedItem.toString()
        val material = materialSpinner.selectedItem.toString()
        val season = seasonSpinner.selectedItem.toString()
        var price = "0.00"
        if (priceEditText.text.toString().isNotEmpty()) {
            val priceInput = priceEditText.text.toString()
            price = priceInput.subSequence(1,priceInput.length).toString().replace(",","")
        }
        val purchaseLocation = purchaseLocationSpinner.selectedItem.toString()
        // gets the clothing entry objects based on the ui input values
        val clothingEntry = Clothing(
            name = itemName,
            category = category,
            material = material,
            season = season,
            price = price.toDouble(),
            purchase_location = purchaseLocation,
            img_uri = imgUriString
        )
        // saves entry to database
        itemDetailsViewModel.insert(clothingEntry)

        // if this is the first time user it will reactivate the footer navigation
        if(sharedPreferences.getBoolean(getString(R.string.first_time_user), true)) {
            val navView = requireActivity().findViewById<CurvedBottomNavigationView>(R.id.nav_view)
            navView.visibility = View.VISIBLE
            navView.setMenuItems(menuItems,1)

            with(sharedPreferences.edit()) {
                putBoolean(getString(R.string.first_time_user), false)
                apply()
            }
        }

        val nextFrag: Fragment? = ClosetFragment()
        if (nextFrag != null) {
            navController.popBackStack()
        }
    }
}