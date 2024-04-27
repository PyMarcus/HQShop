package com.example.hqshop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hqshop.models.UserModel
import com.example.hqshop.util.RegisterFieldState
import com.example.hqshop.util.RegisterValidation
import com.example.hqshop.util.Resource
import com.example.hqshop.util.ValidationCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _createAccountResult = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val createAccountResult: Flow<Resource<FirebaseUser>> = _createAccountResult

    private var _validation = Channel<RegisterFieldState>()
    var validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: UserModel){
        val shouldRegister = checkInputData(user)

        if(shouldRegister){
            runBlocking {
                _createAccountResult.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener {
                it.user?.let {
                    _createAccountResult.value = Resource.Success(it)
                }
            }.addOnFailureListener {
                _createAccountResult.value = Resource.Error(it.message.toString())
            }
        }else{
            val registerFieldState = RegisterFieldState(
                ValidationCheck.validateEmail(user.email),
                ValidationCheck.validatePassword(user.password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun checkInputData(user: UserModel): Boolean{
        val emailValidation = ValidationCheck.validateEmail(user.email)
        val passwordValidation = ValidationCheck.validatePassword(user.password)
        return emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }
}