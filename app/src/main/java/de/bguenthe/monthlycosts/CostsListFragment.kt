package de.bguenthe.monthlycosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import de.bguenthe.monthlycosts.database.AppDatabase
import java.time.format.DateTimeFormatter

class CostsListFragment(): Fragment() {
    internal lateinit var database: AppDatabase
    var year = 0
    var month = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val year = arguments!!.getInt("year")
        val month = arguments!!.getInt("month")
        database = AppDatabase.getDatabase(getActivity()!!.getApplicationContext())
        val costs = database.costsDao().getAllCostsByMonth(year, month)
        val list = ArrayList<String>()
        for (cost in costs) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formattedDate = cost.recordDateTime?.format(formatter)
            list.add("Datum: " + formattedDate + ", Typ: " + cost.type + ", Kosten: " + cost.costs.toString() + ", Beschreibung: " + cost.comment)
        }

        val costsAdapter = ArrayAdapter(
                activity, // Die aktuelle Umgebung (diese Activity)
                R.layout.costs_list_fragment, // ID der XML-Layout Datei
                R.id.cost_name, // ID des TextViews
                list)

        val rootView = inflater.inflate(R.layout.costs_list_activity, container, false)

        val aktienlisteListView = rootView.findViewById(R.id.costs_listview) as ListView
        aktienlisteListView.adapter = costsAdapter

        return rootView
    }
}