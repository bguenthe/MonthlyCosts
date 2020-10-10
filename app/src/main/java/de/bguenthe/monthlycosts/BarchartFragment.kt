package de.bguenthe.monthlycosts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.bguenthe.monthlycosts.chart.ChartEngine
import de.bguenthe.monthlycosts.database.AppDatabase
import de.bguenthe.monthlycosts.database.MonthlyStats

class BarchartFragment : Fragment() {
    internal lateinit var database: AppDatabase
    private lateinit var chartEngine: ChartEngine
    private val constants = Constants()

    companion object {
        fun newInstance(year: Int, month: Int): BarchartFragment {
            val barchartFragmentActivity = BarchartFragment()
            val bundle = Bundle()
            bundle.putInt("year", year)
            bundle.putInt("month", month)
            barchartFragmentActivity.arguments = bundle
            return barchartFragmentActivity
        }

        fun newAverageInstance(): Fragment {
            val barchartFragmentActivity = BarchartFragment()
            val bundle = Bundle()
            bundle.putInt("average", 1)
            barchartFragmentActivity.arguments = bundle
            return barchartFragmentActivity
        }
    }

    data class BarchartObject(val name: String, val value: ArrayList<Float>, val color: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.barchart_fragment, container, false)
        val barchartlist = ArrayList<BarchartObject>()
        chartEngine = ChartEngine(view!!.findViewById(R.id.barchart))
        val average = arguments!!.getInt("average")
        chartEngine.barChart.setOnClickListener {
            val i = Intent(activity, CostsListActivity::class.java)
            i.putExtra("year", arguments!!.getInt("year"))
            i.putExtra("month", arguments!!.getInt("month"))
            startActivity(i)
        }
        database = AppDatabase.getDatabase(getActivity()!!.getApplicationContext())
        lateinit var costs: List<MonthlyStats>
        if (average != 0) {
            val s = this.database.costsDao().getMonthCount()
            costs = this.database.costsDao().getAllMonthlySums()
            val nenner:Double = (s.size - 1).toDouble()
            for (cost in costs) {
                cost.value = cost.value / nenner
            }
        } else {
            costs = database.costsDao().getMonthlySumsPerType(arguments!!.getInt("year"), arguments!!.getInt("month"))
        }

        for (cost in costs) {
            val f = ArrayList<Float>()
            f.add(cost.value.toFloat())
            val barchartObject = BarchartObject(cost.type, f, constants.liste[cost.type]!!.color)
            barchartlist.add(barchartObject)
        }

        val sums = database.costsDao().getMonthlySumOfCosts(arguments!!.getInt("year"), arguments!!.getInt("month"))
        val income = database.incomeDao().getMonthlyIncome(arguments!!.getInt("year"), arguments!!.getInt("month"))
        val fixcosts = database.fixCostsDao().sumOfFixCosts
        val f = ArrayList<Float>()
        f.add(sums[0].value.toFloat()) // Alle Kosten zusammen
        f.add(fixcosts[0].value.toFloat())
        var result = 0f
        if (income != null) {
            result = (income.income - fixcosts[0].value - sums[0].value).toFloat() // Monatsgewinn
        }
        f.add(result)
        if (average == 0) {
            val barchartObject = BarchartObject("sum", f, constants.liste["sum"]!!.color)
            barchartlist.add(barchartObject)
        }
        chartEngine.buildChart(barchartlist, constants, average, arguments!!.getInt("year"), arguments!!.getInt("month"))

        return view
    }
}