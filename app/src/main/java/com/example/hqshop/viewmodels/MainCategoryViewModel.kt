package com.example.hqshop.viewmodels

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.models.ProductModel
import com.example.hqshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Base64
import com.example.hqshop.models.ProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firebase: FirebaseFirestore
) : ViewModel(){

    private val _specialProducts = MutableStateFlow<Resource<List<ProductResult>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<ProductResult>>> = _specialProducts

    init {
        fetch()
    }

    private fun fetch(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firebase.collection("products").whereEqualTo("category", "marvel")
            .get().addOnSuccessListener {response ->
                val products = response.toObjects(ProductModel::class.java)
                val productsResults = mutableListOf<ProductResult>()
                products.forEach{product->
                    product.images.forEach{base64Str->
                        val imageByteArray = Base64.decode(base64Str, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                        val p = ProductResult(
                            product.id,
                            product.name,
                            product.price,
                            product.edition,
                            product.category,
                            product.offerPercentage,
                            product.description,
                            bitmap
                            )
                        productsResults.add(p)
                    }
                }
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(productsResults))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error("Falha ao carregar produtos!"))
                }
            }
    }

}