package com.example.hqshop.views.fragments.access

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hqshop.R
import com.example.hqshop.databinding.FragmentLoginBinding
import com.example.hqshop.dialog.setupBottomSheetDialog
import com.example.hqshop.util.Resource
import com.example.hqshop.viewmodels.LoginViewModel
import com.example.hqshop.views.ShoppingActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.register.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            btnLogin.setOnClickListener{
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.apply {
            forgot.setOnClickListener {
                setupBottomSheetDialog {
                    email -> viewModel.resetPassword(email)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        Intent(
                            // don't allows that comeback to login fragment when the user click on back button
                            Intent(requireActivity(), ShoppingActivity::class.java).also {
                                intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        )
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.btnLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted{
            viewModel.passwordReset.collect{
            when(it){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    Snackbar.make(requireView(),
                        "O link para resetar a senha foi enviado para o seu e-mail",
                        Snackbar.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    Snackbar.make(requireView(),
                        it.message.toString(), Snackbar.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
        }
    }
}