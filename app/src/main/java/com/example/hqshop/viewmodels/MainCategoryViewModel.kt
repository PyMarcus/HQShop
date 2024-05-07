package com.example.hqshop.viewmodels

import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument.PageInfo
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

    private val _bestProducts = MutableStateFlow<Resource<List<ProductResult>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<ProductResult>>> = _bestProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<ProductResult>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<ProductResult>>> = _bestDealsProducts

    private var pageInfo = PagingInfo()

    init {
        fetch()
        fetchBestProducts()
        fetchBestDeals()
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

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firebase.collection("products").limit(pageInfo.page).whereEqualTo("category", "dc")
            .get().addOnSuccessListener {response->
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
                    pageInfo.page++
                }
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(productsResults))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error("Falha ao carregar os melhores produtos!"))
                }
            }
    }

    private fun fetchBestDeals(){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        firebase.collection("products").whereEqualTo("category", "manga")
            .get().addOnSuccessListener {response->
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
                    _bestDealsProducts.emit(Resource.Success(productsResults))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error("Falha ao carregar os melhores produtos!"))
                }
            }
    }

    internal data class PagingInfo(var page: Long = 1)

}