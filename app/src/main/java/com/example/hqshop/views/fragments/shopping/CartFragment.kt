package com.example.hqshop.views.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hqshop.R
import com.example.hqshop.adapters.CartAdapter
import com.example.hqshop.databinding.CartRvItemBinding
import com.example.hqshop.databinding.FragmentCartBinding
import com.example.hqshop.util.Resource
import com.example.hqshop.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CartFragment: Fragment(R.layout.fragment_cart)  {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartAdapter() }
    private val viewModel by viewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
        observers()
    }

    private fun setupRv() {
        binding.rvCart.apply {
            layoutManager=LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
        }
    }

    private fun observers() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartsProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        if(it.data!!.isEmpty()){
                            binding.emptyLayout.visibility = View.VISIBLE
                        }else{
                            binding.emptyLayout.visibility = View.GONE
                            cartAdapter.differ.submitList(it.data)
                        }
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        binding.emptyLayout.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showLoading(){
        binding.loading.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.loading.visibility = View.GONE
    }
}