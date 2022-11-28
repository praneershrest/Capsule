package com.example.capsule.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.capsule.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class MaterialStatsFragment: Fragment() {
    private lateinit var chart: PieChart
    private lateinit var entries: ArrayList<PieEntry>
    private lateinit var colours: ArrayList<Int>
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_material_stats, container, false)
        chart = view.findViewById(R.id.material_pie_chart)
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
        // initialize data
        // TODO: fetch data from db and combine synthetic materials
        var materialData: MutableMap<String, Int> = HashMap()
        materialData["Cotton"] = 40
        materialData["Leather"] = 5
        materialData["Wool"] = 10
        materialData["Rubber"] = 6
        materialData["Acrylic"] = 12
        materialData["Nylon"] = 2
        materialData["Polyester"] = 40

        colours = resources.getIntArray(R.array.material_graph_colours).toList() as ArrayList<Int>
        entries = ArrayList()
        for (material in materialData.keys) {
            entries.add(PieEntry(materialData.get(material)!!.toFloat(), material))
        }

        pieDataSet = PieDataSet(entries, "Type")
        pieDataSet.valueTextSize = 12f
        pieDataSet.colors = colours
        pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        pieData.setValueFormatter(PercentFormatter(chart))

        chart.data = pieData

    }


}