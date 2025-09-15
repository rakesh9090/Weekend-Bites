package com.demo.assignmentapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.assignmentapplication.databinding.ActivityMainBinding
import com.demo.assignmentapplication.ui.adapter.ViewPagerAdapter
import com.demo.assignmentapplication.util.Constant.HOLDINGS
import com.demo.assignmentapplication.util.Constant.POSITION
import com.demo.assignmentapplication.util.Constant.TAB
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)


        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> HOLDINGS
                1 -> POSITION
                else -> "$TAB $position"
            }
        }.attach()
    }
}