package com.example.hqshop.util

import android.util.Patterns

class ValidationCheck {

    companion object{
        fun validateEmail(email: String): RegisterValidation{
            if(email.isEmpty())
                return RegisterValidation.Failed("Informe um e-mail!")

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return RegisterValidation.Failed("Formato de e-mail inválido!")

            return RegisterValidation.Success
        }

        fun validatePassword(password: String): RegisterValidation{
            if(password.isEmpty())
                return RegisterValidation.Failed("Informe uma senha!")

            if(password.length < 6)
                return RegisterValidation.Failed("A senha deve ter, pelo menos, 6 caracteres!")

            return RegisterValidation.Success
        }
    }
}