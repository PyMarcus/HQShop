package com.example.hqshop.views.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hqshop.R
import com.example.hqshop.adapters.BestDealsAdapter
import com.example.hqshop.adapters.BestProductsAdapter
import com.example.hqshop.adapters.SpecialProductsAdapter
import com.example.hqshop.databinding.FragmentMainCategoryBinding
import com.example.hqshop.util.Resource
import com.example.hqshop.util.showBottomNavigationView
import com.example.hqshop.viewmodels.CartViewModel
import com.example.hqshop.viewmodels.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private val viewModel by viewModels<MainCategoryViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestProductsRv()
        setupBestDealsRv()

        observers()

        bestDealsAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, p)
        }
        bestProductsAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, p)
        }
        specialProductsAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, p)
        }

    }

    private fun setupBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false)
        binding.rvBestProducts.adapter = bestProductsAdapter
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvBestDealsProducts.adapter = bestDealsAdapter

    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.rvSpecialProducts.adapter = specialProductsAdapter
    }

    private fun observers() {
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hiddenLoading()
                    }
                    is Resource.Error -> {
                        hiddenLoading()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.loadingbestproducts.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.loadingbestproducts.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.loadingbestproducts.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        hiddenLoading()
                    }
                    is Resource.Error -> {
                        hiddenLoading()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }

        binding.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v, _, scrolly, _, _ ->
            if(v.getChildAt(0).bottom <= v.height + scrolly){
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE
    }

    private fun hiddenLoading(){
        binding.loading.visibility = View.GONE
    }


}