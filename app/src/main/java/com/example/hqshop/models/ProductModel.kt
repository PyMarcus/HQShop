package com.example.hqshop.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ProductModel(
    val id: String,
    val name: String,
    val price: Float,
    val edition: Int,
    val category: String,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val images: List<String>
){
    constructor():this("0", "", 0F, 0, "", null, "", listOf())
}


@Parcelize
data class ProductResult(
    val id: String,
    val name: String,
    val price: Float,
    val edition: Int,
    val category: String,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val images: Bitmap?
): Parcelable{
    constructor():this("0", "", 0F, 0, "", null, "",null)
}