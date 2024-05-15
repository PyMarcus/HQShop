package com.example.hqshop.views.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hqshop.R
import com.example.hqshop.adapters.BestProductsAdapter
import com.example.hqshop.databinding.FragmentSearchBinding
import com.example.hqshop.models.ProductResult
import com.example.hqshop.util.Resource
import com.example.hqshop.util.hideBottomNavigationView
import com.example.hqshop.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<SearchViewModel> ()
    private var arrayCopy = mutableListOf<ProductResult>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        hideBottomNavigationView()
        backToOrigin()
        searching()
        observers()

        // carrega informacoes do produto :D
        bestProductsAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_searchFragment_to_productDetailFragment, p)
        }
    }

    private fun observers() {
        lifecycleScope.launchWhenStarted {
            viewModel.items.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        arrayCopy = bestProductsAdapter.differ.currentList
                        hiddenLoading()
                    }
                    is Resource.Error -> {
                        hiddenLoading()
                        Toast.makeText(requireContext(), "Falha ao buscar produtos!", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun hiddenLoading() {
        binding.loading.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE
    }

    private fun setupListAdapter() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvSearchedProducts.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL,
            false)
        binding.rvSearchedProducts.adapter = bestProductsAdapter
    }

    private fun backToOrigin(){
        binding.close.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun searching() {
        binding.searchNow.clearFocus()
        binding.searchNow.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filterList(newText)
                    return true
                }
            })
    }

    private fun filterList(text: String) {
        val filtered = bestProductsAdapter.differ.currentList.filter {
            it.name.contains(text, ignoreCase = true)
        }
        bestProductsAdapter.differ.submitList(filtered)
        if(filtered.isEmpty() or text.isNullOrEmpty() or text.isBlank())
            bestProductsAdapter.differ.submitList(arrayCopy)
    }
}