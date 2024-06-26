package com.example.hqshop.views.fragments.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hqshop.R
import com.example.hqshop.models.CategoryModel
import com.example.hqshop.util.Resource
import com.example.hqshop.util.showBottomNavigationView
import com.example.hqshop.viewmodels.CategoryViewModel
import com.example.hqshop.viewmodels.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class DisneyCategoryFragment : BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore


    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, CategoryModel.Disney)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bestProductsAdapter.onClick = {
            val p = Bundle().apply { putParcelable("ProductResult", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, p)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Falha ao carregar", Snackbar.LENGTH_SHORT)
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showBestProductsLoading()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error -> {
                        hideBestProductsLoading()
                        Snackbar.make(requireView(), "Falha ao carregar", Snackbar.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onPagingBestProductsCategoryRequest() {

    }

    override fun onPagingOfferCategoryRequest() {

    }
}