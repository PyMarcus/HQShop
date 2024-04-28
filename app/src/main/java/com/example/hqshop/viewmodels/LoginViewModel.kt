package com.example.hqshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.models.UserModel
import com.example.hqshop.util.Constants.LOGIN_INCORRECT_MESSAGE
import com.example.hqshop.util.RegisterValidation
import com.example.hqshop.util.Resource
import com.example.hqshop.util.ValidationCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private var firebaseAuth: FirebaseAuth
): ViewModel(){

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _passwordReset = MutableSharedFlow<Resource<String>>()
    val passwordReset = _passwordReset.asSharedFlow()


    fun login(email: String, password: String){
        val shouldLogin = checkInputData(email, password)
        if(shouldLogin){
            viewModelScope.launch {
                _login.emit(Resource.Loading())
            }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        it.user?.let {
                            _login.emit(Resource.Success(it))
                        }
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(LOGIN_INCORRECT_MESSAGE))
                    }
                }
        }else{
            viewModelScope.launch {
                _login.emit(Resource.Error(LOGIN_INCORRECT_MESSAGE))
            }
        }

    }

    private fun checkInputData(email: String, password: String): Boolean{
        val emailValidation = ValidationCheck.validateEmail(email)
        val passwordValidation = ValidationCheck.validatePassword(password)
        return emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }

    fun resetPassword(email: String){
        viewModelScope.launch {
            _passwordReset.emit(Resource.Loading())
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _passwordReset.emit(Resource.Success(email))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _passwordReset.emit(Resource.Error("Falha ao enviar e-mail!"))
                }
            }
    }
}