package de.bguenthe.monthlycosts.chart

import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import de.bguenthe.monthlycosts.BarchartFragment
import de.bguenthe.monthlycosts.Constants


class ChartEngine// no right axis
// no left axis
// start at zero
(internal var barChart: BarChart) {

    init {
        this.barChart.setDrawBarShadow(false)
        this.barChart.setDrawValueAboveBar(true)
        val yAxis = this.barChart.axisLeft
        this.barChart.axisRight.isEnabled = false
        this.barChart.axisLeft.isEnabled = false
        val xAxis = this.barChart.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        yAxis.axisMinimum = 0f
        this.barChart.setPinchZoom(false)
        this.barChart.setDrawGridBackground(true)
        this.barChart.legend.isEnabled = false
        this.barChart.description.isEnabled = true
    }

    fun buildChart(monthlyCosts: ArrayList<BarchartFragment.BarchartObject>, constants: Constants, average: Int, year: Int, month: Int) {
        barChart.data?.clearValues()
        barChart.xAxis.valueFormatter = null
        barChart.notifyDataSetChanged()
        barChart.clear()
        barChart.invalidate()
        barChart.description.textSize = 100f
        barChart.description.textColor = Color.BLACK
        barChart.description.setPosition(100f, 150f)
        barChart.description.textAlign = Paint.Align.LEFT
        if (average == 0) {
            barChart.description.text = year.toString() + "." + month.toString()
        } else { // durchschitt
            barChart.description.text = "Monatlicher Durchschnitt"
        }
   
        var max = 0f
        for (costs in monthlyCosts) {
            if (costs.value[0] > max)
                max = costs.value[0]
        }
        val bars: ArrayList<IBarDataSet> = ArrayList()
        for ((i, costs) in monthlyCosts.withIndex()) {
            val be = BarEntry(i.toFloat(), costs.value.toFloatArray())
            val entries: ArrayList<BarEntry> = ArrayList()
            entries.add(be)
            val barDataSet = BarDataSet(entries, "")
            barDataSet.valueTextSize = 12f
            if (i < monthlyCosts.size - 1) {
                barDataSet.setColor(constants.liste[costs.name]!!.color)
            } else { // durchschnitt
                val colors = ArrayList<Int>()
                for (lcosts in monthlyCosts) {
                    colors.add(constants.liste[lcosts.name]!!.color)
                }
                barDataSet.setColors(
                        Color.rgb(255, 0, 0), // GRÜN
                        Color.rgb(38, 203, 217), //BLAU
                        Color.rgb(0, 255, 0) // GRÜN
                )
            }
            bars.add(barDataSet)
        }

        val formatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return monthlyCosts[value.toInt()].name
            }
        }

        val xAxis = this.barChart.xAxis
        xAxis.granularity = 1f
        xAxis.valueFormatter = formatter

        val barData = BarData(bars)
        barData.barWidth = 0.9f
        this.barChart.data = barData
        this.barChart.notifyDataSetChanged()
        this.barChart.invalidate()
    }
}