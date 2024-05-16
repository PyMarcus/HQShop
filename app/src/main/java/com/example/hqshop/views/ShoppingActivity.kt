package com.example.hqshop.views

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hqshop.R
import com.example.hqshop.databinding.ActivityShoppingBinding
import com.example.hqshop.util.Resource
import com.example.hqshop.viewmodels.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy{
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shopping_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)

        observerCartQnt()
    }

    private fun observerCartQnt() {
        lifecycleScope.launch {
            viewModel.cartsProducts.collectLatest {
                when(it){
                    is Resource.Success ->{
                        val count = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_red)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}