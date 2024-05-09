package com.example.hqshop.viewmodels

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.models.CategoryModel
import com.example.hqshop.models.ProductModel
import com.example.hqshop.models.ProductResult
import com.example.hqshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CategoryViewModel(private val firestore: FirebaseFirestore, private val category: CategoryModel) :
ViewModel(){

    private val _offerProducts = MutableStateFlow<Resource<List<ProductResult>>>(Resource.Unspecified())
    var offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<ProductResult>>>(Resource.Unspecified())
    var bestProducts = _bestProducts.asStateFlow()


    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts(){
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        firestore.collection("products")
            .whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener {response->
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
                    _offerProducts.emit(Resource.Success(productsResults))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error("Falha ao carregar os produtos"))
                }
            }
    }

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("products")
            .whereEqualTo("category", category.category)
            .whereEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener {response->
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
                    _bestProducts.emit(Resource.Success(productsResults))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error("Falha ao carregar os produtos"))
                }
            }
    }

    internal data class PagingInfo(
        var page: Long = 1,
        var isPageEnd: Boolean = false,
        var actualList: List<ProductModel> = emptyList()
    )

}