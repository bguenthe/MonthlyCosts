package de.bguenthe.monthlycosts

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BarchartPagerAdapter(fm: FragmentManager?, private val numberOfMonthsToShow: Int) : FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        if (position == numberOfMonthsToShow - 1) {
            return BarchartFragment.newAverageInstance()
        } else
            return BarchartFragment.newInstance(LocalDate.now().minus((numberOfMonthsToShow - 1 - (position + 1)).toLong(), ChronoUnit.MONTHS).year, LocalDate.now().minus((numberOfMonthsToShow - 1 - (position + 1)).toLong(), ChronoUnit.MONTHS).monthValue)
    }

    override fun getCount(): Int {
        return numberOfMonthsToShow
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "CONSTANT"
    }
}
