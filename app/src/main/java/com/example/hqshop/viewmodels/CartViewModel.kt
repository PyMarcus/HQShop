package com.example.hqshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.models.Cart
import com.example.hqshop.models.ProductResult
import com.example.hqshop.repository.FirebaseCommonRepository
import com.example.hqshop.util.Convert
import com.example.hqshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommonRepository: FirebaseCommonRepository
): ViewModel(){

    private var _cartsProducts = MutableStateFlow<Resource<List<ProductResult>>>(Resource.Unspecified())
    var cartsProducts = _cartsProducts.asStateFlow()
    private var cartDocuments = emptyList<DocumentSnapshot>()

    init {
        getProductsCard()
    }

    private fun getProductsCard(){
        viewModelScope.launch { _cartsProducts.emit(Resource.Loading()) }
        // observa o carrinho, qualquer alteracao, sera chamado
        firestore.collection("users").document(auth.uid!!)
            .collection("cart").addSnapshotListener{value, error->
                if(error != null || value == null){
                    viewModelScope.launch {
                        _cartsProducts.emit(Resource.Error("Falha ao carregar itens do carrinho!"))
                    }
                }else{
                    viewModelScope.launch {
                        cartDocuments = value.documents
                        val items = value.toObjects(Cart::class.java)
                        var products = mutableListOf<ProductResult>()
                        items.forEach{
                            products.add(Convert.convertToProductResult(it.product))
                        }
                        _cartsProducts.emit(Resource.Success(products))
                    }
                }
            }
    }

    fun changeQuantity(product: ProductResult, quantityChanging: FirebaseCommonRepository.QuantityChanging){
        val productIndex: Int? = cartsProducts.value.data?.indexOf(product)
        if((productIndex != null) && (productIndex >= 0)){
            val documentId: String = cartDocuments[productIndex].id
            when(quantityChanging){
                FirebaseCommonRepository.QuantityChanging.INCREASE -> {
                    firebaseCommonRepository.increaseQuantity(documentId){ result, error ->
                        if(error != null){
                            viewModelScope.launch { _cartsProducts.emit(Resource.Error("Houve um problema")) }
                        }
                    }
                }
                FirebaseCommonRepository.QuantityChanging.DECREASE -> {
                    firebaseCommonRepository.decreaseQuantity(documentId){ result, error->
                        if(error != null){
                            viewModelScope.launch { _cartsProducts.emit(Resource.Error("Houve um problema")) }
                        }
                    }
                }
            }
        }
    }


}