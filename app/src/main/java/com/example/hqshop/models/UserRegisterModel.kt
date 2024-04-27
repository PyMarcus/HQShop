package com.example.hqshop.models

data class UserRegisterModel(
    var firstName: String,
    var lastName: String,
    var email: String,
    var imagePath: String = ""
)
