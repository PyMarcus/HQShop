package com.example.hqshop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hqshop.models.UserModel
import com.example.hqshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _createAccountResult = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val createAccountResult: Flow<Resource<FirebaseUser>> = _createAccountResult

    fun createAccountWithEmailAndPassword(user: UserModel){
        println("ENTROU AQUI")
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener {
            it.user?.let {
                _createAccountResult.value = Resource.Success(it)
            }
        }.addOnFailureListener {
            it.message?.let {
                _createAccountResult.value = Resource.Error(it)
            }
        }
    }
}