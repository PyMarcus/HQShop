package com.example.hqshop.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.hqshop.models.ProductModel
import com.example.hqshop.models.ProductResult
import com.example.hqshop.models.UserBitmap
import com.example.hqshop.models.UserModel
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

        fun convertToProductResult(productModel: ProductModel):ProductResult{
            return ProductResult(
                productModel.id,
                productModel.name,
                productModel.price,
                productModel.edition,
                productModel.category,
                productModel.offerPercentage,
                productModel.description,
                base64ToBitmap(productModel.images)
            )
        }

        fun convertToUserBitMap(user: UserModel):UserBitmap{
            return UserBitmap(
                user.firstName,
                user.lastName,
                user.email,
                user.password,
                userBase64ToBitmap(user.image)
            )
        }

        private fun bitmapToBase64(bitmap: Bitmap?): List<String> {
            val outputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()
            return listOf(Base64.encodeToString(imageBytes, Base64.DEFAULT))
        }

        private fun base64ToBitmap(base: List<String>): Bitmap {
            val imageBytes = Base64.decode(base[0], Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

        private fun userBase64ToBitmap(base: String?): Bitmap? {
            if(base != null){
                val imageBytes = Base64.decode(base, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }
            return null
        }
    }
}