package com.example.hqshop.repository

import com.example.hqshop.models.Cart
import com.example.hqshop.models.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommonRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("users")
        .document(auth.uid!!).collection("cart")

    fun addProductToCart(cart: Cart, onResult: (Cart?, Exception?) -> Unit){
            cartCollection.document().set(cart).addOnSuccessListener {
                onResult(cart, null)
            }.addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(documentId: String, onResult: (String?,Exception?) -> Unit){
        firestore.runTransaction {transaction->
            val selectProduct = cartCollection.document(documentId)
            val product = transaction.get(selectProduct).toObject(Cart::class.java)
            product?.let {cart->
                val newQnt = cart.quantity + 1
                val productUpdated = cart.copy(quantity = newQnt)
                transaction.set(selectProduct, productUpdated)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

}
