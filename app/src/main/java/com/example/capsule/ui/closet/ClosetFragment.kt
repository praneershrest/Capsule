package com.example.capsule.ui.closet

import android.app.Activity
import android.content.Intent
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.R
import com.example.capsule.Util
import com.example.capsule.databinding.FragmentClosetBinding
import com.example.capsule.ui.itemDetails.ItemDetailsFragment
import java.io.File

// TODO - Improve styling of the fragment
class ClosetFragment : Fragment() {

    private var _binding: FragmentClosetBinding? = null
    private lateinit var noInventoryView: ViewStub
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var imgUri: Uri
    private lateinit var imgFile: File
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


        noInventoryView = root.findViewById(R.id.noInventoryScreen)
        noInventoryView.inflate(); // inflate the layout
        noInventoryView.visibility = View.INVISIBLE;

        // TODO - Change this to if there are things in database
        var pass = true
        if (pass) {
            var mainScreen = root.findViewById<RelativeLayout>(R.id.mainClosetScreen)
            mainScreen.visibility = View.INVISIBLE
            noInventoryView.visibility = View.VISIBLE;
        }

        val textView: TextView = binding.textDashboard
        closetViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                noInventoryView.visibility = View.INVISIBLE

                val nextFrag = ItemDetailsFragment()
                val args = Bundle()
                args.putSerializable(R.string.img_filename_key.toString(), imgFile)
                args.putString(R.string.img_uri_key.toString(), imgUri.toString())
                nextFrag.arguments = args

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, nextFrag, R.string.item_details_fragment_key.toString())
                    .commit()
            }
        }
        var takePhotoBtn: Button = view.findViewById(R.id.take_photo_btn)
        takePhotoBtn.setOnClickListener {
            onTakePhoto()
        }
    }


    fun onTakePhoto() {
        imgFile = Util.createImageFile(requireActivity())
        imgUri = FileProvider.getUriForFile(requireActivity(), "com.example.capsule", imgFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        cameraResult.launch(intent)
    }

    fun onUploadPhoto() {

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