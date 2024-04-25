package com.example.hqshop.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hqshop.R
import com.example.hqshop.databinding.FragmentRegisterBinding
import com.example.hqshop.models.UserModel
import com.example.hqshop.util.Resource
import com.example.hqshop.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.observeOn

@AndroidEntryPoint
class RegisterFragment: Fragment(), OnClickListener {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleEvents()

        observers()
    }

    override fun onClick(v: View) {
        when(v.id){
            binding.btnRegister.id -> register()
        }
    }

    private fun handleEvents(){
        binding.btnRegister.setOnClickListener(this)
    }

    private fun register(){
        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        val user = UserModel(firstName, lastName, email, password)

        viewModel.createAccountWithEmailAndPassword(user)
    }

    private fun observers(){
        lifecycleScope.launchWhenStarted {
            viewModel.createAccountResult.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        println("OK SUCESS" + it.message.toString())
                        binding.btnRegister.revertAnimation()
                    }
                    is Resource.Error -> {
                        println("OK FAIL " +  it.message.toString())
                        binding.btnRegister.revertAnimation()
                    }
                }
            }
        }
    }
}