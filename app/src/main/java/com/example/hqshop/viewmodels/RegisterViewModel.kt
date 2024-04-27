package com.example.hqshop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hqshop.models.UserModel
import com.example.hqshop.models.UserRegisterModel
import com.example.hqshop.util.Constants.USER_COLLECTION
import com.example.hqshop.util.RegisterFieldState
import com.example.hqshop.util.RegisterValidation
import com.example.hqshop.util.Resource
import com.example.hqshop.util.ValidationCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel(){

    private val _createAccountResult = MutableStateFlow<Resource<UserRegisterModel>>(Resource.Unspecified())
    val createAccountResult: Flow<Resource<UserRegisterModel>> = _createAccountResult

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
                    val userRegister = UserRegisterModel(user.firstName, user.lastName,
                        user.email)
                    saveUserInfo(it.uid, userRegister)
                    _createAccountResult.value = Resource.Success(userRegister)
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

    private fun saveUserInfo(userId: String, user: UserRegisterModel){
        db.collection(USER_COLLECTION)
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                _createAccountResult.value  = Resource.Success(user)
            }.addOnFailureListener{
                _createAccountResult.value = Resource.Error(it.message.toString())
            }
    }
}