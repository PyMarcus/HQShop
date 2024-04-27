package com.example.hqshop.util

sealed class RegisterValidation() {
    object Success: RegisterValidation()
    data class Failed(var message: String): RegisterValidation()
}

data class RegisterFieldState(
    var email: RegisterValidation,
    var password: RegisterValidation
)