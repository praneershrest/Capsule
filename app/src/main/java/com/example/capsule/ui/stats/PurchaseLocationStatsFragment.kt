package com.example.capsule.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.capsule.R
import com.example.capsule.database.ClothingDatabase
import com.example.capsule.database.ClothingDatabaseDao
import com.example.capsule.database.ClothingHistoryDatabaseDao
import com.example.capsule.database.Repository
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class PurchaseLocationStatsFragment: Fragment() {
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var historyDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var repository: Repository
    private lateinit var factory: StatsViewModelFactory

    private lateinit var purchaseLocationFrequencyList: List<PurchaseLocationFrequency>

    private lateinit var chart: PieChart
    private lateinit var entries: ArrayList<PieEntry>
    private lateinit var colours: ArrayList<Int>
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

    private lateinit var emptyStateIcon: ImageView
    private lateinit var emptyStateHeader: TextView
    private lateinit var emptyStateDescription: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_location_stats, container, false)
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        historyDatabaseDao = database.clothingHistoryDatabaseDao
        repository = Repository(clothingDatabaseDao, historyDatabaseDao)
        factory = StatsViewModelFactory(repository)

        emptyStateIcon = view.findViewById(R.id.purchase_loc_empty_icon)
        emptyStateHeader = view.findViewById(R.id.purchase_loc_empty_state_header)
        emptyStateDescription = view.findViewById(R.id.purchase_loc_empty_state_description)

        entries = ArrayList()
        purchaseLocationFrequencyList = ArrayList()

        chart = view.findViewById(R.id.purchase_loc_pie_chart)
        statsViewModel = ViewModelProvider(requireActivity(), factory).get(StatsViewModel::class.java)
        statsViewModel.purchaseLocationFrequencies.observe(requireActivity()) {
            purchaseLocationFrequencyList = it
            displayPieChart()
        }

        initPieChart()
        displayPieChart()
        return view
    }

    private fun initPieChart() {
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.holeRadius = 0f
        chart.transparentCircleRadius = 0f
        chart.minAngleForSlices = 20f
        chart.legend.isEnabled = false
    }

    private fun displayPieChart() {
        if (purchaseLocationFrequencyList.isNotEmpty()) {
            emptyStateIcon.visibility = View.GONE
            emptyStateHeader.visibility = View.GONE
            emptyStateDescription.visibility = View.GONE
            chart.visibility = View.VISIBLE

            colours = resources.getIntArray(R.array.purchase_loc_graph_colours).toList() as ArrayList<Int>
            entries = ArrayList()
            for (location in purchaseLocationFrequencyList) {
                entries.add(PieEntry(location.frequency.toFloat(), location.purchase_location))
            }

            pieDataSet = PieDataSet(entries, "Type")
            pieDataSet.valueTextSize = 12f
            pieDataSet.colors = colours
            pieData = PieData(pieDataSet)
            pieData.setDrawValues(true)
            pieData.setValueFormatter(PercentFormatter(chart))

            chart.data = pieData
            chart.invalidate()
        } else {
            chart.visibility = View.GONE
            emptyStateIcon.visibility = View.VISIBLE
            emptyStateHeader.visibility = View.VISIBLE
            emptyStateDescription.visibility = View.VISIBLE
        }
    }

}