package com.example.capsule.ui.closet

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.capsule.R
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.FragmentClosetBinding
import com.example.capsule.model.Clothing
import com.example.capsule.ui.itemDetails.ItemDetailsFragment
import com.example.capsule.utils.Util
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


// TODO - Improve styling of the fragment
class ClosetFragment : Fragment() {

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClosetBinding.inflate(inflater, container, false)
        root = binding.root
        Util.checkPermissions(requireActivity())

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = ClosetViewModelFactory(databaseRepository)

        viewPager2 = root.findViewById(R.id.closet_viewpager)
        clothingDescriptionListView = root.findViewById(R.id.clothingDetailsList)
        clothesTitle = root.findViewById(R.id.clothes_title)
        costPerWear = root.findViewById(R.id.price_per_wear)
        emptyStateIcon = root.findViewById(R.id.closet_empty_icon)
        emptyStateHeader = root.findViewById(R.id.closet_empty_state)
        emptyStateDescription = root.findViewById(R.id.closet_empty_description)

        clothingDescriptionItems = ArrayList()
        categoryList = resources.getStringArray(R.array.category_items).toList()
        removeBtn = root.findViewById(R.id.remove_item_btn)

        val closetViewModel =
            ViewModelProvider(this, factory).get(ClosetViewModel::class.java)

        closetViewModel.allClothingEntriesLiveData.observe(requireActivity()){
            allClothingEntries = it
        }

        closetViewModel.topsFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Tops"){
                allFrequencies = it
                removeEmptyState()
                if (isAdded && allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE
                }  else {
                    displayEmptyState(R.drawable.tshirt)
                    removeBtn.visibility = View.INVISIBLE
                }
            }
        }

        closetViewModel.bottomsFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Bottoms"){
                allFrequencies = it
                removeEmptyState()
                if (isAdded && allFrequencies.isNotEmpty()) {
                    loadData(0)
                    instantiateHorizontalScrollView()
                    removeBtn.visibility = View.VISIBLE
                }  else {
                    displayEmptyState(R.drawable.trousers)
                    removeBtn.visibility = View.INVISIBLE
                }
            }
        }

        closetViewModel.outerwearFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Outerwear"){
                allFrequencies = it
                removeEmptyState()
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

        closetViewModel.shoesFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Shoes"){
                allFrequencies = it
                removeEmptyState()
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

        tabLayout = root.findViewById(R.id.tab)
        for (category in categoryList) {
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }

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

        noInventoryView = root.findViewById(R.id.noInventoryScreen)
        noInventoryView.inflate() // inflate the layout
        noInventoryView.visibility = View.INVISIBLE

        if (sharedPreferences.getBoolean(getString(R.string.first_time_user), true)) {
            val mainScreen = root.findViewById<LinearLayout>(R.id.mainClosetScreen)
            mainScreen.visibility = View.INVISIBLE
            noInventoryView.visibility = View.VISIBLE
            noInventoryView.alpha = 0.5f
        }

        val addItemBtn: View = root.findViewById(R.id.add_item_btn)
        addItemBtn.setOnClickListener { view ->
            noInventoryView.visibility = View.VISIBLE
        }

        val removeItemBtn: View = root.findViewById(R.id.remove_item_btn)
        removeItemBtn.setOnClickListener { view ->
            createDialog(closetViewModel)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                startNextFragment()
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val intent = result.data
                if (intent != null) {
                    val galleryUri = intent.data!!
                    val bitmap = Util.getBitmap(requireActivity(), galleryUri)
                    // Referenced from https://stackoverflow.com/questions/18080474/download-an-image-file-and-replace-existing-image-file
                    try {
                        val out = FileOutputStream(imgFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()

                        startNextFragment()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val takePhotoBtn: Button = view.findViewById(R.id.take_photo_btn)
        takePhotoBtn.setOnClickListener {
            noInventoryView.visibility = View.INVISIBLE
            onTakePhoto()
        }

        val uploadPhotoBtn: Button = view.findViewById(R.id.upload_photo_btn)
        uploadPhotoBtn.setOnClickListener {
            noInventoryView.visibility = View.INVISIBLE
            onUploadPhoto()
        }
    }

    private fun deleteItem(closetViewModelLocal : ClosetViewModel) {
        closetViewModelLocal.remove(allFrequencies[currScrollPos].id)
    }

    private fun createDialog(closetViewModelLocal : ClosetViewModel){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val customLayout: View = layoutInflater.inflate(R.layout.fragment_confirmation_dialog, null)
        var textView: TextView = customLayout.findViewById(R.id.dialogText)
        var submitBtn: Button = customLayout.findViewById(R.id.dialog_submit_btn)
        var cancelBtn: Button = customLayout.findViewById(R.id.dialog_cancel_btn)
        submitBtn.text = getString(R.string.yes_btn_msg)
        cancelBtn.text = getString(R.string.cancel_btn_msg)
        textView.text = getString(R.string.remove_item_prompt)
        builder.setView(customLayout)

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
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.show()
    }

    private fun loadData(idx: Int){
        clothesTitle = root.findViewById(R.id.clothes_title)
        costPerWear = root.findViewById(R.id.price_per_wear)
        clothesTitle.text = ""
        costPerWear.text = ""
        clothingDescriptionItems.clear()

        if (allFrequencies.isNotEmpty()) {
            val itemWearFreq = allFrequencies[idx]

            clothesTitle.text = itemWearFreq.name
            costPerWear.text = computePricePerWear(itemWearFreq.price, itemWearFreq.frequency)

            clothingDescriptionItems.add(Pair("Category", itemWearFreq.category))
            clothingDescriptionItems.add(Pair("Material", itemWearFreq.material))
            clothingDescriptionItems.add(Pair("Season", itemWearFreq.season))
            clothingDescriptionItems.add(Pair("Price", "$${String.format("%.2f", itemWearFreq.price)}"))
            clothingDescriptionItems.add(Pair("Purchase Location", itemWearFreq.purchase_location))

            clothingDescriptionListView = root.findViewById(R.id.clothingDetailsList)
            clothingDescriptionListView.adapter = ClothingListAdapter(requireActivity(), clothingDescriptionItems)
        }
    }

    private fun computePricePerWear(price: Double, freq: Int): String {
        val costPerWear = (price / freq)
        return "$${String.format("%.2f", costPerWear)} / wear"
    }


    private fun instantiateHorizontalScrollView() {
        sliderItems = ArrayList()

        allFrequencies.forEach {
            val imgUri = Uri.parse(it.img_uri)
            val bitmap = Util.getBitmap(requireActivity(), imgUri)
            sliderItems.add(SliderItem(bitmap))
        }

        viewPager2 = root.findViewById(R.id.closet_viewpager)
        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit=3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currScrollPos = position
                loadData(position)
            }
        })
    }

    private fun onTakePhoto() {
        imgFile = Util.createImageFile(requireActivity())
        imgUri = FileProvider.getUriForFile(requireActivity(), "com.example.capsule", imgFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        cameraResult.launch(intent)
    }

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