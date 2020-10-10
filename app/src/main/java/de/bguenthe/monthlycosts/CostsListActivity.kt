package de.bguenthe.monthlycosts

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


class CostsListActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.costs_list_activity)
        val year = getIntent().getIntExtra("year", 0)
        val month = getIntent().getIntExtra("month", 0)
        val bundle = Bundle()
        bundle.putInt("year", year)
        bundle.putInt("month", month)

        val manager = supportFragmentManager
        var fragment: Fragment? = manager.findFragmentById(R.id.fragmentContainer)

        if (fragment == null) {
            fragment = CostsListFragment()
            fragment.setArguments(bundle)
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit()
        }
    }
}