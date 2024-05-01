package com.example.hqshop.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class HomeViewPageAdapter(
    private val fragments: List<Fragment>,
    private val fm: FragmentManager,
    private val lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle){
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}