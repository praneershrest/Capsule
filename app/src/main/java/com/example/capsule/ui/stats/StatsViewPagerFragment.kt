package com.example.capsule.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.capsule.R
import com.example.capsule.databinding.FragmentStatsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class StatsViewPagerFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: StatsFragmentStateAdapter
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var materialStatsFragment: MaterialStatsFragment
    private lateinit var purchaseLocationStatsFragment: PurchaseLocationStatsFragment
    private lateinit var frequencyStatsFragment: FrequencyStatsFragment
    private lateinit var tabLayout: TabLayout
    private lateinit var tabLayoutMediator: TabLayoutMediator

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Set up fragments for each tab
        materialStatsFragment = MaterialStatsFragment()
        purchaseLocationStatsFragment = PurchaseLocationStatsFragment()
        frequencyStatsFragment = FrequencyStatsFragment()

        fragments = ArrayList()
        fragments.addAll(listOf(materialStatsFragment, purchaseLocationStatsFragment, frequencyStatsFragment))

        //Set up ViewPager for swiping between fragments
        val view: View = inflater.inflate(context?.resources?.getLayout(R.layout.fragment_stats), container, false)
        viewPager = view.findViewById(R.id.stats_viewpager)
        viewPagerAdapter = StatsFragmentStateAdapter(requireActivity(), fragments)
        viewPager.adapter = viewPagerAdapter
        tabLayout = view.findViewById(R.id.stats_tab_dots)
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager){ tab, position -> }
        tabLayoutMediator.attach()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
        _binding = null
    }
}