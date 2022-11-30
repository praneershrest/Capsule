package com.example.capsule.ui.closet

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.capsule.R
import com.example.capsule.Util
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.example.capsule.databinding.FragmentClosetBinding
import com.example.capsule.model.Clothing
import com.example.capsule.ui.itemDetails.ItemDetailsFragment
import com.example.capsule.ui.stats.ItemWearFrequency
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.selects.select
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.abs

// TODO - Improve styling of the fragment
class ClosetFragment : Fragment() {

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
    private lateinit var allFrequencies: List<ItemWearFrequency>

    private var selectedTab = 0

    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var clothingHistoryDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var databaseRepository: Repository
    private lateinit var factory: ClosetViewModelFactory
    private lateinit var clothesTitle: TextView
    private lateinit var costPerWear: TextView
    private lateinit var categoryList: List<String>

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

        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        clothingHistoryDatabaseDao = database.clothingHistoryDatabaseDao
        databaseRepository = Repository(clothingDatabaseDao, clothingHistoryDatabaseDao)
        factory = ClosetViewModelFactory(databaseRepository)

        clothingDescriptionItems = ArrayList()
        categoryList = resources.getStringArray(R.array.category_items).toList()

        val closetViewModel =
            ViewModelProvider(this, factory).get(ClosetViewModel::class.java)

        closetViewModel.allClothingEntriesLiveData.observe(requireActivity()){
            allClothingEntries = it
        }

        closetViewModel.topsFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Tops"){
                allFrequencies = it
                //loadData(0)
                // instantiateHorizontalScrollView()
            }
        }

        closetViewModel.bottomsFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Bottoms"){
                allFrequencies = it
                // loadData(0)
                // instantiateHorizontalScrollView()
            }
        }

        closetViewModel.outerwearFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Outerwear"){
                allFrequencies = it
                // loadData(0)
                // instantiateHorizontalScrollView()
            }
        }

        closetViewModel.shoesFrequenciesLiveData.observe(requireActivity()) {
            if (categoryList[selectedTab] == "Shoes"){
                allFrequencies = it
                // loadData(0)
                // instantiateHorizontalScrollView()
            }
        }

        clothesTitle = root.findViewById(R.id.clothes_title)
        costPerWear = root.findViewById(R.id.price_per_wear)

        tabLayout = root.findViewById(R.id.tab)
        for (category in categoryList) {
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTab = tab!!.position
                when (categoryList[selectedTab]) {
                    "Tops" -> {
                        allFrequencies = closetViewModel.topsFrequenciesLiveData.value!!
                        loadData(0)
                        instantiateHorizontalScrollView()
                    }
                    "Bottoms" -> {
                        allFrequencies = closetViewModel.bottomsFrequenciesLiveData.value!!
                        loadData(0)
                        instantiateHorizontalScrollView()
                    }
                    "Outerwear" -> {
                        allFrequencies = closetViewModel.outerwearFrequenciesLiveData.value!!
                        loadData(0)
                        instantiateHorizontalScrollView()
                    }
                    "Shoes" -> {
                        allFrequencies = closetViewModel.shoesFrequenciesLiveData.value!!
                        loadData(0)
                        instantiateHorizontalScrollView()
                    }
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

        // TODO - Change this to if there are things in database
        var pass = false
        if (pass) {
            var mainScreen = root.findViewById<RelativeLayout>(R.id.mainClosetScreen)
            mainScreen.visibility = View.INVISIBLE
            noInventoryView.visibility = View.VISIBLE
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
                    var galleryUri = intent.data!!
                    val bitmap = Util.getBitmap(requireActivity(), galleryUri)
                    // Referenced from https://stackoverflow.com/questions/18080474/download-an-image-file-and-replace-existing-image-file
                    try {
                        var out = FileOutputStream(imgFile)
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

        var takePhotoBtn: Button = view.findViewById(R.id.take_photo_btn)
        takePhotoBtn.setOnClickListener {
            onTakePhoto()
        }

        var uploadPhotoBtn: Button = view.findViewById(R.id.upload_photo_btn)
        uploadPhotoBtn.setOnClickListener {
            onUploadPhoto()
        }
    }

    private fun loadData(idx: Int){
        println("idx here $idx")
        clothingDescriptionItems.clear()
        if (allFrequencies.isNotEmpty()) {
            var itemWearFreq = allFrequencies[idx]

            clothesTitle.text = itemWearFreq.name
            costPerWear.text = computePricePerWear(itemWearFreq.price, itemWearFreq.frequency)

            clothingDescriptionItems.add(Pair("Category", itemWearFreq.category))
            clothingDescriptionItems.add(Pair("Material", itemWearFreq.material))
            clothingDescriptionItems.add(Pair("Season", itemWearFreq.season))
            clothingDescriptionItems.add(Pair("Price", itemWearFreq.price.toString()))
            clothingDescriptionItems.add(Pair("Purchase Location", itemWearFreq.purchase_location))

            clothingDescriptionListView = root.findViewById(R.id.clothingDetailsList)
            clothingDescriptionListView.adapter = ClothingListAdapter(requireActivity(), clothingDescriptionItems)
        }
    }

    private fun computePricePerWear(price: Double, freq: Int) : String{
        val costPerWear = (price / freq)
        val formattedCostPerWearString = "$${String.format("%.2f", costPerWear)} / wear"
        return formattedCostPerWearString
    }


    private fun instantiateHorizontalScrollView() {
        sliderItems = ArrayList()

        allFrequencies.forEach {
            var imgUri = Uri.parse(it.img_uri)
            var bitmap = Util.getBitmap(requireActivity(), imgUri)
            sliderItems.add(SliderItem(bitmap))
        }

        viewPager2 = root.findViewById(R.id.viewPager1)
        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit=3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        var compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("position $position")
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

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.closetFragment, nextFrag, R.string.item_details_fragment_key.toString())
            .commit()
    }

    private fun onUploadPhoto() {
        imgFile = Util.createImageFile(requireActivity())
        imgUri = FileProvider.getUriForFile(requireActivity(), "com.example.capsule", imgFile)
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)
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