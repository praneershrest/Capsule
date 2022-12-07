package com.example.capsule.ui.closet

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.capsule.R
import com.example.capsule.camera.CameraActivity
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.FragmentClosetBinding
import com.example.capsule.model.Clothing
import com.example.capsule.ui.itemDetails.ItemDetailsFragment
import com.example.capsule.utils.Util
import com.google.android.material.tabs.TabLayout
import java.io.*

/**
 * Closet Fragment for displaying the saved clothing items by retrieving from database
 */
class ClosetFragment : Fragment() {

    // initializing variables
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentClosetBinding? = null
    private lateinit var root: View
    private lateinit var noInventoryView: ViewStub
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var imgUri: Uri
    private lateinit var imgFile: File
    private lateinit var tabLayout: TabLayout
    private lateinit var sliderItems: ArrayList<SliderItem>
    private lateinit var clothingDescriptionItems: ArrayList<Pair<String, String>>
    private lateinit var viewPager2: ViewPager2
    private lateinit var clothingDescriptionListView: ListView
    private lateinit var allClothingEntries: List<Clothing>
    private lateinit var allFrequencies: List<ClosetItemData>

    private var selectedTab = 0
    private var currScrollPos = 0

    private lateinit var emptyStateIcon: ImageView
    private lateinit var emptyStateHeader: TextView
    private lateinit var emptyStateDescription: TextView

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: ClosetViewModelFactory
    private lateinit var clothesTitle: TextView
    private lateinit var costPerWear: TextView
    private lateinit var categoryList: List<String>
    private lateinit var removeBtn: Button
    private lateinit var topsListInitalize: List<ClosetItemData>
    private var firstRun = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding the root to the view before displaying
        _binding = FragmentClosetBinding.inflate(inflater, container, false)
        root = binding.root

        // get users camera and photo gallery
        Util.checkPermissions(requireActivity())

        // get database and shared preferences for backend data
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = ClosetViewModelFactory(databaseRepository)

        // get views from the xml based on id
        viewPager2 = root.findViewById(R.id.closet_viewpager)
        clothingDescriptionListView = root.findViewById(R.id.clothingDetailsList)
        clothesTitle = root.findViewById(R.id.clothes_title)
        costPerWear = root.findViewById(R.id.price_per_wear)
        emptyStateIcon = root.findViewById(R.id.closet_empty_icon)
        emptyStateHeader = root.findViewById(R.id.closet_empty_state)
        emptyStateDescription = root.findViewById(R.id.closet_empty_description)

        clothingDescriptionItems = ArrayList()

        // get string titles from the string resources
        categoryList = resources.getStringArray(R.array.category_items).toList()
        // reset page
        removeBtn = root.findViewById(R.id.remove_item_btn)
        selectedTab = 0

        // closet view model to get all clothes
        val closetViewModel =
            ViewModelProvider(this, factory).get(ClosetViewModel::class.java)

        closetViewModel.allClothingEntriesLiveData.observe(requireActivity()){
            allClothingEntries = it
        }

        // get all the tops from database
        closetViewModel.topsFrequenciesLiveData.observe(requireActivity()) {
            // since tops defaults when you open page we instantiate a local tops page
            // to load when the page opens on the first time
            topsListInitalize = it
            if (categoryList[selectedTab] == "Tops"){
                // initialize displayed list into the horizontal scroll
                allFrequencies = it

                removeEmptyState()
                // put images into horizontal carosal view when allfrequencies not empty
                if (isAdded && allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE
                }  else {
                    // else display empty state
                    displayEmptyState(R.drawable.tshirt)
                    removeBtn.visibility = View.INVISIBLE
                }
            }
        }

        // get all the bottoms from database
        closetViewModel.bottomsFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Bottoms"){
                allFrequencies = it
                removeEmptyState()
                // initialize displayed list into the horizontal scroll
                if (isAdded && allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE
                }  else {
                    // else display empty state
                    displayEmptyState(R.drawable.trousers)
                    removeBtn.visibility = View.INVISIBLE
                }
            }
        }

        // get all the outerwear from database
        closetViewModel.outerwearFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Outerwear"){
                // initialize displayed list into the horizontal scroll
                allFrequencies = it
                removeEmptyState()
                // put images into horizontal carosal view when allfrequencies not empty
                if (isAdded && allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE
                }  else {
                    displayEmptyState(R.drawable.coat)
                    removeBtn.visibility = View.INVISIBLE

                }
            }
        }

        // get all the shoes from database
        closetViewModel.shoesFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Shoes"){
                // initialize displayed list into the horizontal scroll
                allFrequencies = it
                removeEmptyState()
                // put images into horizontal carosal view when allfrequencies not empty
                if (isAdded && allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE
                }  else {
                    displayEmptyState(R.drawable.sandal)
                    removeBtn.visibility = View.INVISIBLE

                }
            }
        }

        // instantiate tab titles with tab strings
        tabLayout = root.findViewById(R.id.tab)
        for (category in categoryList) {
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }

        // Eventlistener for when the tab changes, reinstantitate the list with
        // the respective clothing category
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var categoryImg = R.drawable.tshirt
                selectedTab = tab!!.position
                when (categoryList[selectedTab]) {
                    "Tops" -> {
                        allFrequencies = closetViewModel.topsFrequenciesLiveData.value!!
                    }
                    "Bottoms" -> {
                        allFrequencies = closetViewModel.bottomsFrequenciesLiveData.value!!
                        categoryImg = R.drawable.trousers
                    }
                    "Outerwear" -> {
                        allFrequencies = closetViewModel.outerwearFrequenciesLiveData.value!!
                        categoryImg = R.drawable.coat
                    }
                    "Shoes" -> {
                        allFrequencies = closetViewModel.shoesFrequenciesLiveData.value!!
                        categoryImg = R.drawable.sandal

                    }
                }
                removeEmptyState()
                // reload the screen with new items based on category
                if (allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE

                } else {
                    displayEmptyState(categoryImg)
                    removeBtn.visibility = View.INVISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // this is a special viewstub component that acts as a dialog fragment
        // displays upload photo or take photo
        noInventoryView = root.findViewById(R.id.noInventoryScreen)
        noInventoryView.inflate() // inflate the layout
        // it will first be turned to invisible
        noInventoryView.visibility = View.INVISIBLE

        // if its the first time user the viewstub will be set to visible
        if (sharedPreferences.getBoolean(getString(R.string.first_time_user), true)) {
            val mainScreen = root.findViewById<LinearLayout>(R.id.mainClosetScreen)
            mainScreen.visibility = View.INVISIBLE
            noInventoryView.visibility = View.VISIBLE
            noInventoryView.alpha = 0.5f
        }

        // initialize top right add item btn
        val addItemBtn: View = root.findViewById(R.id.add_item_btn)
        addItemBtn.setOnClickListener { view ->
            noInventoryView.visibility = View.VISIBLE
        }

        // create a dialog btn for removing items when on click remove btn
        removeBtn.setOnClickListener { view ->
            createDialog(closetViewModel)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // cameraresult call back function when user takes a photo
        // save photo and send to next fragment
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data!!
                imgUri = data.getParcelableExtra("uri")!!
                imgFile = data.getSerializableExtra("file") as File
                startNextFragment()
            }
        }

        // gallery result call back for when user chooses photo
        // save photo and send to next fragment
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val intent = result.data
                if (intent != null) {
                    val galleryUri = intent.data!!
                    try {
                        // copy image to local database and display based on uri
                        // save uri
                        val inputStream = requireActivity().contentResolver.openInputStream(galleryUri)
                        val out = FileOutputStream(imgFile)
                        if (inputStream != null) {
                            copyStream(inputStream, out)
                            inputStream.close()
                        }
                        out.flush()
                        out.close()

                        startNextFragment()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        // actual take photo btn
        val takePhotoBtn: Button = view.findViewById(R.id.take_photo_btn)
        takePhotoBtn.setOnClickListener {
            noInventoryView.visibility = View.INVISIBLE
            onTakePhoto()
        }

        // actual upload photo btn
        val uploadPhotoBtn: Button = view.findViewById(R.id.upload_photo_btn)
        uploadPhotoBtn.setOnClickListener {
            noInventoryView.visibility = View.INVISIBLE
            onUploadPhoto()
        }
    }

    // delete item from database and also delete image from files
    private fun deleteItem(closetViewModelLocal : ClosetViewModel) {
        // gets passed uri
        val imgToDeleteUri = Uri.parse(allFrequencies[currScrollPos].img_uri)
        // delete from database
        closetViewModelLocal.remove(allFrequencies[currScrollPos].id)

        // check if files exist, if it exists, delete from external, else delete from internal
        val file = File(imgToDeleteUri.path!!)
        if (file.exists()){
            file.delete()
        }
        else {
            val contentResolver: ContentResolver = requireActivity().contentResolver
            contentResolver.delete(imgToDeleteUri, null, null)
        }
    }

    // this is a custom dialog fragment that acts as a verification for when users deletes items
    private fun createDialog(closetViewModelLocal : ClosetViewModel){
        // creates builder for dialog  and customize ui of dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val customLayout: View = layoutInflater.inflate(R.layout.fragment_confirmation_dialog, null)
        val textView: TextView = customLayout.findViewById(R.id.dialogText)
        val submitBtn: Button = customLayout.findViewById(R.id.dialog_submit_btn)
        val cancelBtn: Button = customLayout.findViewById(R.id.dialog_cancel_btn)
        submitBtn.text = getString(R.string.yes_btn_msg)
        cancelBtn.text = getString(R.string.cancel_btn_msg)
        textView.text = getString(R.string.remove_item_prompt)
        builder.setView(customLayout)

        // call back from dialog on click activitys
        val dialog: AlertDialog = builder.create()
        submitBtn.setOnClickListener{
            dialog.dismiss()
            Toast.makeText(requireActivity().applicationContext, getString(R.string.toast_msg_removed), Toast.LENGTH_SHORT).show()
            deleteItem(closetViewModelLocal)
        }
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

    // function for copying image from the gallery to internal files
    @Throws(IOException::class)
    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }


    // load data initializes all the text data taken from the database
    private fun loadData(idx: Int){

        // get ui elements
        clothesTitle = root.findViewById(R.id.clothes_title)
        costPerWear = root.findViewById(R.id.price_per_wear)
        clothesTitle.text = ""
        costPerWear.text = ""
        clothingDescriptionItems.clear()

        // get the datalist from database and display the information at curr idx
        if (allFrequencies.isNotEmpty()) {
            val itemWearFreq = allFrequencies[idx]

            clothesTitle.text = itemWearFreq.name
            costPerWear.text = computePricePerWear(itemWearFreq.price, itemWearFreq.frequency)

            clothingDescriptionItems.add(Pair("Material", itemWearFreq.material))
            clothingDescriptionItems.add(Pair("Season", itemWearFreq.season))
            clothingDescriptionItems.add(Pair("Price", "$${String.format("%,.2f", itemWearFreq.price)}"))
            clothingDescriptionItems.add(Pair("Purchase Location", itemWearFreq.purchase_location))

            clothingDescriptionListView = root.findViewById(R.id.clothingDetailsList)
            clothingDescriptionListView.adapter = ClothingListAdapter(requireActivity(), clothingDescriptionItems)
        }
    }

    private fun computePricePerWear(price: Double, freq: Int): String {
        val costPerWear = (price / freq)
        return "$${String.format("%,.2f", costPerWear)} / wear"
    }


    // this functions instantiates everything in the carasol based on database uris
    private fun instantiateHorizontalScrollView() {
        sliderItems = ArrayList()

        // for each of the items in database, get the uri and put the images into the carasol
        allFrequencies.forEach {
            val imgUri = it.img_uri.toUri()
            sliderItems.add(SliderItem(imgUri))
        }

        // set up for carousal
        viewPager2 = root.findViewById(R.id.closet_viewpager)
        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit=3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        // this is used for  carousal
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        // onevent listener for when someone scrolls on the horizontalfor carousal
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // on the first run it needs to render the tops page
                if (firstRun) {
                    allFrequencies = topsListInitalize
                    firstRun = true
                }
                currScrollPos = position
                // rerender the pages
                loadData(position)
            }
        })
    }

    private fun onTakePhoto() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        cameraResult.launch(intent)
    }

    // function to pass things to next fragment and navigate there
    private fun startNextFragment() {
        noInventoryView.visibility = View.INVISIBLE
        val nextFrag = ItemDetailsFragment()
        val args = Bundle()
        args.putSerializable(R.string.img_filename_key.toString(), imgFile)
        args.putString(R.string.img_uri_key.toString(), imgUri.toString())
        nextFrag.arguments = args

        val navController = root.findNavController()
        navController.navigate(R.id.action_navigation_closet_to_itemDetailsFragment, args)

    }

    private fun onUploadPhoto() {
        imgFile = Util.createImageFile(requireActivity())
        imgUri = FileProvider.getUriForFile(requireActivity(), "com.example.capsule", imgFile)
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)
    }

    /// resets ui components
    private fun displayEmptyState(image: Int) {
        viewPager2.visibility = View.GONE
        clothingDescriptionListView.visibility = View.GONE
        clothesTitle.visibility = View.GONE
        costPerWear.visibility = View.GONE
        emptyStateIcon.setImageResource(image)
        emptyStateIcon.visibility = View.VISIBLE
        emptyStateHeader.visibility = View.VISIBLE
        emptyStateDescription.visibility = View.VISIBLE
    }

    // removes ui components
    private fun removeEmptyState() {
        emptyStateIcon.visibility = View.GONE
        emptyStateHeader.visibility = View.GONE
        emptyStateDescription.visibility = View.GONE
        viewPager2.visibility = View.VISIBLE
        clothingDescriptionListView.visibility = View.VISIBLE
        clothesTitle.visibility = View.VISIBLE
        costPerWear.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val fragment: Fragment? = requireActivity().supportFragmentManager.findFragmentByTag(R.string.item_details_fragment_key.toString())
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
        _binding = null
    }
}