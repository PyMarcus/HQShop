package com.example.hqshop.util

import android.graphics.Bitmap
import android.util.Base64
import com.example.hqshop.models.ProductModel
import com.example.hqshop.models.ProductResult
import java.io.ByteArrayOutputStream

class Convert {
    companion object{
        fun convertToProductModel(productResult: ProductResult):ProductModel{
            return ProductModel(
                productResult.id,
                productResult.name,
                productResult.price,
                productResult.edition,
                productResult.category,
                productResult.offerPercentage,
                productResult.description,
                bitmapToBase64(productResult.images)
            )
        }

        private fun bitmapToBase64(bitmap: Bitmap?): List<String> {
            val outputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()
            return listOf(Base64.encodeToString(imageBytes, Base64.DEFAULT))
        }
    }
}