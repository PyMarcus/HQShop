package com.example.hqshop.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.hqshop.R
import com.example.hqshop.views.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNav = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottom_navigation
    )
    bottomNav.visibility = View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNav = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottom_navigation
    )
    bottomNav.visibility = View.VISIBLE
}