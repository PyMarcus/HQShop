package com.example.hqshop.models

data class CartModel(
    val product: ProductResult,
    val quantity: Int
) {
    constructor() : this(ProductResult(), 1)
}

data class Cart(
    val product: ProductModel,
    val quantity: Int
) {
    constructor() : this(ProductModel(), 1)
}