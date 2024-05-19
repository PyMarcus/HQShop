package com.example.hqshop.views.fragments.shopping

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hqshop.R
import com.example.hqshop.databinding.FragmentProfileBinding
import com.example.hqshop.util.Resource
import com.example.hqshop.viewmodels.ProfileViewModel
import com.example.hqshop.views.LoginRegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            imageUri = it.data?.data
            Glide.with(this).load(imageUri).into(binding.profileImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserInfo()
        logout()
        close()
        editImage()
        saveImage()
        observers()
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModel.updateImage.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUserInfo(){
        lifecycleScope.launch {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                        binding.btnSave.startAnimation()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        binding.btnSave.revertAnimation()
                        if(it.data != null){
                            binding.edName.text = it.data.firstName
                            binding.edEmail.text = it.data.email
                            Glide.with(requireContext()).load(it.data.image).into(binding.profileImage)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun saveImage() {
        binding.btnSave.setOnClickListener {
            viewModel.updateUserImage(imageUri)
        }
    }

    private fun editImage() {
        binding.editImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
    }

    private fun close(){
        binding.close.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun logout(){
        binding.logout.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun hideLoading() {
        binding.loading.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Deseja mesmo sair?")

        builder.setPositiveButton("OK") { dialog, _ ->
            viewModel.logout()
            dialog.dismiss()
            val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
            startActivity(intent)
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}