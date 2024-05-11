package com.example.hqshop.views.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hqshop.R
import com.example.hqshop.adapters.BestProductsAdapter
import com.example.hqshop.databinding.FragmentBaseCategoryBinding
import com.example.hqshop.util.Resource
import com.example.hqshop.util.showBottomNavigationView
import com.example.hqshop.viewmodels.CategoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProductsRv()


        // loading product info if it was clicked
        bestProductsAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, p)
        }

        offerAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, p)
        }

        // scrolling
        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!recyclerView.canScrollVertically(1) && dx != 0){
                    onPagingOfferCategoryRequest()
                }
            }
        })

        binding.nestedCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrolly, _, _ ->
            if(v.getChildAt(0).bottom <= v.height + scrolly){
                onPagingBestProductsCategoryRequest()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }


    open fun onPagingOfferCategoryRequest(){

    }

    open fun onPagingBestProductsCategoryRequest(){

    }

    fun showOfferLoading(){
        binding.offerProgressbar.visibility = View.VISIBLE
    }

    fun showBestProductsLoading(){
        binding.bestProgressbar.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.offerProgressbar.visibility = View.GONE
    }

    fun hideBestProductsLoading(){
        binding.bestProgressbar.visibility = View.GONE
    }


    private fun setupBestProductsRv() {
        binding.rvBestProducts.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false)
        binding.rvBestProducts.adapter = bestProductsAdapter
    }

    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
                )
        }
        binding.rvOffer.adapter = offerAdapter
    }
}