package com.example.hqshop.models

import android.graphics.Bitmap

data class UserModel(
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var image: String?
)
{
    constructor():this(firstName="", lastName="", email="", password="", image=null)
}

data class UserBitmap(
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var image: Bitmap?
)
{
    constructor():this(firstName="", lastName="", email="", password="", image=null)
}