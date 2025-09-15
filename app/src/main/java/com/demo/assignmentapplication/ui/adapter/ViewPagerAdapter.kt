package com.demo.assignmentapplication.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.demo.assignmentapplication.ui.HoldingsFragment
import com.demo.assignmentapplication.ui.PositionsFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HoldingsFragment()
            1 -> PositionsFragment()
            else -> Fragment()
        }
    }
}
