package com.example.hqshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.models.Cart
import com.example.hqshop.models.CartModel
import com.example.hqshop.models.ProductModel
import com.example.hqshop.repository.FirebaseCommonRepository
import com.example.hqshop.util.Convert
import com.example.hqshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommonRepository: FirebaseCommonRepository
): ViewModel(){

    private val _addToCart = MutableStateFlow<Resource<Cart>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addAndUpdateProductsInCart(cart: Cart){

        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }

        firestore.collection("users").document(auth.uid!!)
            .collection("cart")
            .whereEqualTo("product.id", cart.product.id)
            .get()
            .addOnSuccessListener {
                it.documents.let {
                    if(it.isEmpty()){
                        addNewProduct(cart)
                    }else{
                        var product = it.first().toObject(Cart::class.java)
                        println("PRODUCT ${product!!.product.name} CART ${cart.product.name}")
                        if(product.product.id == cart.product.id){
                            val docId = it.first().id
                            println("PRODUCT doc id ${docId}")
                            increaseQnt(docId, cart)
                        }else{
                            addNewProduct(cart)
                        }
                    }
                }
            }
            .addOnFailureListener {error->
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error("Falha ao adicionar produto ao carrinho! : ${error.message.toString()}"))
                }
            }
    }

    private fun addNewProduct(cart: Cart){
        firebaseCommonRepository.addProductToCart(cart){ addedProduct, e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(e.toString()))
                }
            }
        }
    }

    private fun increaseQnt(id: String, cart: Cart){
        firebaseCommonRepository.increaseQuantity(id){_, e ->
            viewModelScope.launch {
                if(e == null)
                    _addToCart.emit(Resource.Success(cart))
                else
                    _addToCart.emit(Resource.Error(e.toString()))
            }
        }
    }

}