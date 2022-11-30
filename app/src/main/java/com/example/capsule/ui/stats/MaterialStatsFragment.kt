package com.example.capsule.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class MaterialStatsFragment: Fragment() {
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var database: ClothingDatabase
    private lateinit var clothingDatabaseDao: ClothingDatabaseDao
    private lateinit var historyDatabaseDao: ClothingHistoryDatabaseDao
    private lateinit var repository: Repository
    private lateinit var factory: StatsViewModelFactory

    private lateinit var chart: PieChart
    private lateinit var entries: ArrayList<PieEntry>
    private lateinit var colours: ArrayList<Int>
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData
    private lateinit var materialFrequencyList: List<MaterialFrequency>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_material_stats, container, false)
        database = ClothingDatabase.getInstance(requireActivity())
        clothingDatabaseDao = database.clothingDatabaseDao
        historyDatabaseDao = database.clothingHistoryDatabaseDao
        repository = Repository(clothingDatabaseDao, historyDatabaseDao)
        factory = StatsViewModelFactory(repository)

        entries = ArrayList()
        materialFrequencyList = ArrayList()

        statsViewModel = ViewModelProvider(requireActivity(), factory).get(StatsViewModel::class.java)
        statsViewModel.materialFrequencies.observe(requireActivity()) {
            materialFrequencyList = it
            displayPieChart()
        }

        chart = view.findViewById(R.id.material_pie_chart)
        initPieChart()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayPieChart()
    }

    override fun onResume() {
        super.onResume()
        statsViewModel.materialFrequencies.observe(requireActivity()) {
            materialFrequencyList = it
            displayPieChart()
        }
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
        colours = resources.getIntArray(R.array.material_graph_colours).toList() as ArrayList<Int>
        entries = ArrayList()
        for (material in materialFrequencyList) {
            entries.add(PieEntry(material.frequency.toFloat(), material.material))
        }

        pieDataSet = PieDataSet(entries, "Type")
        pieDataSet.valueTextSize = 12f
        pieDataSet.colors = colours
        pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        pieData.setValueFormatter(PercentFormatter(chart))

        chart.data = pieData
        chart.invalidate()
    }

}