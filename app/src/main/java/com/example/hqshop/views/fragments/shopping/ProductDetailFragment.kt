package com.example.hqshop.views.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hqshop.adapters.ViewPager2ImagesAdapter
import com.example.hqshop.databinding.FragmentProductDetailBinding
import com.example.hqshop.models.Cart
import com.example.hqshop.models.CartModel
import com.example.hqshop.util.Convert
import com.example.hqshop.util.Resource
import com.example.hqshop.util.hideBottomNavigationView
import com.example.hqshop.viewmodels.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment: Fragment() {
    private val args: ProductDetailFragmentArgs by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding: FragmentProductDetailBinding
    private val viewPagerAdapter by lazy { ViewPager2ImagesAdapter() }
    private val viewModel by viewModels<ProductDetailViewModel>()

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

        binding.addToCart.setOnClickListener {
            viewModel.addAndUpdateProductsInCart(
                Cart(Convert.convertToProductModel(product), 1)
            )
        }

        observers()
    }

    private fun setupViewPager() {
        binding.apply {
            viewpager.adapter = viewPagerAdapter
        }
    }

    private fun observers(){
        lifecycleScope.launch {
           viewModel.addToCart.collectLatest {
               when(it){
                   is Resource.Loading -> {
                       binding.addToCart.startAnimation()
                   }
                   is Resource.Success -> {
                       binding.addToCart.revertAnimation()
                       Toast.makeText(requireContext(), "Adicionado!",
                           Toast.LENGTH_SHORT).show()
                   }
                   is Resource.Error -> {
                       binding.addToCart.revertAnimation()
                       Toast.makeText(requireContext(), "Falha ao colocar produto no carrinho",
                           Toast.LENGTH_SHORT).show()
                   }
                   else -> Unit

               }
           }
        }
    }
}
