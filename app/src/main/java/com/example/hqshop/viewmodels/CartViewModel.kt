package com.example.hqshop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.models.Cart
import com.example.hqshop.models.CartModel
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommonRepository: FirebaseCommonRepository
): ViewModel(){

    private var _cartsProducts = MutableStateFlow<Resource<List<CartModel>>>(Resource.Unspecified())
    var cartsProducts = _cartsProducts.asStateFlow()
    private var cartDocuments = emptyList<DocumentSnapshot>()
    var cartTotalPrice = cartsProducts.map {
        when(it){
            is Resource.Success ->{
                solveTotal(it.data!!)
            }
            else -> "R$ 0,00"
        }
    }


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
                        var products = mutableListOf<CartModel>()
                        items.forEach{
                            products.add(CartModel(Convert.convertToProductResult(it.product), it.quantity))
                        }
                        _cartsProducts.emit(Resource.Success(products))
                    }
                }
            }
    }

    fun changeQuantity(product: CartModel, quantityChanging: FirebaseCommonRepository.QuantityChanging){
        val productIndex: Int? = cartsProducts.value.data?.indexOf(product)
        if((productIndex != null) && (productIndex >= 0)){
            val documentId: String = cartDocuments[productIndex].id
            when(quantityChanging){
                FirebaseCommonRepository.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartsProducts.emit(Resource.Loading()) }
                    firebaseCommonRepository.increaseQuantity(documentId){ result, error ->
                        if(error != null){
                            viewModelScope.launch { _cartsProducts.emit(Resource.Error("Houve um problema")) }
                        }
                    }
                }
                FirebaseCommonRepository.QuantityChanging.DECREASE -> {
                    if(product.quantity == 1){
                        val documentId: String = cartDocuments[productIndex].id
                        deleteProduct(documentId)
                        return
                    }
                    viewModelScope.launch { _cartsProducts.emit(Resource.Loading()) }
                    firebaseCommonRepository.decreaseQuantity(documentId){ result, error->
                        if(error != null){
                            viewModelScope.launch { _cartsProducts.emit(Resource.Error("Houve um problema")) }
                        }
                    }
                }
            }
        }
    }

    private fun deleteProduct(documentId: String) {
        firestore.collection("users").document(auth.uid!!)
            .collection("cart").document(documentId).delete()

    }

    private fun solveTotal(cart: List<CartModel>): String{
        var total = 0.0F
        cart.forEach{cartCollection ->
            val product = cartCollection.product
            val discount =  product.offerPercentage
            total += if(discount != null){
                val newPrice = (product.price - (
                        product.price * discount) * cartCollection.quantity)
                newPrice
            }else{
                product.price * cartCollection.quantity
            }
                }
        return "R$ ${total.toString().replace(".", ",")}"
    }


}