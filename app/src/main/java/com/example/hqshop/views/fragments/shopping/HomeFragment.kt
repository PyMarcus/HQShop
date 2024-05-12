package com.example.hqshop.views.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hqshop.R
import com.example.hqshop.adapters.HomeViewPageAdapter
import com.example.hqshop.databinding.FragmentHomeBinding
import com.example.hqshop.views.fragments.category.DCCategoryFragment
import com.example.hqshop.views.fragments.category.DisneyCategoryFragment
import com.example.hqshop.views.fragments.category.MainCategoryFragment
import com.example.hqshop.views.fragments.category.MangaCategoryFragment
import com.example.hqshop.views.fragments.category.MarvelCategoryFragment
import com.example.hqshop.views.fragments.category.TurmaDaMonicaCategoryFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentsCategory = arrayListOf<Fragment>(
            MainCategoryFragment(),
            MarvelCategoryFragment(),
            DCCategoryFragment(),
            MangaCategoryFragment(),
            DisneyCategoryFragment(),
            TurmaDaMonicaCategoryFragment()
        )

        val homeViewerAdapter = HomeViewPageAdapter(fragmentsCategory, childFragmentManager, lifecycle)
        binding.homeviewer.adapter = homeViewerAdapter

        binding.homeviewer.isUserInputEnabled = false // remove slide to another page

        binding.searchBar.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        TabLayoutMediator(binding.tablayout, binding.homeviewer){ tab, position ->
            when(position){
                0 -> tab.text = "Principal"
                1 -> tab.text = "Marvel"
                2 -> tab.text = "DC Universe"
                3 -> tab.text = "Mangás"
                4 -> tab.text = "Disney"
                5 -> tab.text = "Turma da mônica"
            }
        }.attach()

    }

}