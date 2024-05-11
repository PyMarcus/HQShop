package com.example.hqshop.views.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hqshop.R
import com.example.hqshop.adapters.ViewPager2ImagesAdapter
import com.example.hqshop.databinding.FragmentProductDetailBinding
import com.example.hqshop.util.hideBottomNavigationView
import com.example.hqshop.views.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductDetailFragment: Fragment() {
    private val args: ProductDetailFragmentArgs by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding: FragmentProductDetailBinding
    private val viewPagerAdapter by lazy { ViewPager2ImagesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.productResult
        setupViewPager()


        binding.apply {
            productName.text = product.name
            price.text = "R$ ${product.price.toString().replace(".", ",")}"
            product.description?.let {
                productDescription.text = it
            }?: run {
                productDescription.text = ""
            }
        }
        binding.imgClose.setOnClickListener{
            findNavController().navigateUp()
        }
        viewPagerAdapter.differ.submitList(listOf(product.images))
    }

    private fun setupViewPager() {
        binding.apply {
            viewpager.adapter = viewPagerAdapter
        }
    }
}
