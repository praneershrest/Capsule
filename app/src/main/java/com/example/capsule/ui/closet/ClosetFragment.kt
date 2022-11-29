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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.capsule.R
import com.example.capsule.Util
import com.example.capsule.databinding.FragmentClosetBinding
import com.example.capsule.ui.itemDetails.ItemDetailsFragment
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Math.abs

// TODO - Improve styling of the fragment
class ClosetFragment : Fragment() {

    private var _binding: FragmentClosetBinding? = null
    private lateinit var noInventoryView: ViewStub
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var imgUri: Uri
    private lateinit var imgFile: File
    private lateinit var tabLayout: TabLayout
    private lateinit var sliderItems: ArrayList<SliderItem>
    private lateinit var viewPager2: ViewPager2

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val closetViewModel =
            ViewModelProvider(this).get(ClosetViewModel::class.java)
        _binding = FragmentClosetBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Util.checkPermissions(requireActivity())

        sliderItems = ArrayList()
        sliderItems.add(SliderItem(R.drawable.ic_launcher_background))
        sliderItems.add(SliderItem(R.drawable.ic_launcher_background))
        sliderItems.add(SliderItem(R.drawable.ic_launcher_background))
        sliderItems.add(SliderItem(R.drawable.ic_launcher_background))
        sliderItems.add(SliderItem(R.drawable.ic_launcher_background))
        sliderItems.add(SliderItem(R.drawable.ic_launcher_background))
        tabLayout = root.findViewById(R.id.tab)

        viewPager2 = root.findViewById(R.id.viewPager1)
        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit=3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        var compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(ViewPager2.PageTransformer(){ page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.95f + r *  0.05f
        })

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                println("lol" + position)
                super.onPageSelected(position)
            }
        })


        val tabTitles = resources.getStringArray(R.array.category_items)

        tabLayout.addTab(tabLayout.newTab().setText(tabTitles[0]))
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles[1]))
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles[2]))
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles[3]))



        noInventoryView = root.findViewById(R.id.noInventoryScreen)
        noInventoryView.inflate(); // inflate the layout
        noInventoryView.visibility = View.INVISIBLE;

        // TODO - Change this to if there are things in database
        var pass = false
        if (pass) {
            var mainScreen = root.findViewById<RelativeLayout>(R.id.mainClosetScreen)
            mainScreen.visibility = View.INVISIBLE
            noInventoryView.visibility = View.VISIBLE;
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
                        out.flush();
                        out.close();

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


    fun onTakePhoto() {
        imgFile = Util.createImageFile(requireActivity())
        imgUri = FileProvider.getUriForFile(requireActivity(), "com.example.capsule", imgFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        cameraResult.launch(intent)
    }

    fun startNextFragment() {
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

    fun onUploadPhoto() {
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