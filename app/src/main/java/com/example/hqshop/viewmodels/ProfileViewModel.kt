package com.example.hqshop.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hqshop.HQShopApplication
import com.example.hqshop.models.UserBitmap
import com.example.hqshop.models.UserModel
import com.example.hqshop.util.Constants.USER_COLLECTION
import com.example.hqshop.util.Convert
import com.example.hqshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    app: Application
): AndroidViewModel(app) {
    private val _user = MutableStateFlow<Resource<UserBitmap>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateImage = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val updateImage = _updateImage.asStateFlow()


    init {
        getUserInfo()
    }

    private fun getUserInfo(){
        viewModelScope.launch{
            _user.emit(Resource.Loading())
        }
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .get().addOnSuccessListener {
                val user = it.toObject(UserModel::class.java)
                user?.let {u->
                    viewModelScope.launch{
                        val userB = Convert.convertToUserBitMap(user)
                        _user.emit(Resource.Success(userB))
                    }
                }
            }
    }

    fun updateUserImage(imageUri: Uri?){
        viewModelScope.launch { _updateImage.emit(Resource.Loading()) }
        if(imageUri != null){
           viewModelScope.launch{
               try {
                   val bitmap = MediaStore.Images.Media.getBitmap(
                       getApplication<HQShopApplication>().contentResolver,
                       imageUri
                   )
                   val byteArrayOutputStream = ByteArrayOutputStream()
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                   val imageByteArray = byteArrayOutputStream.toByteArray()
                   val imageBase64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                   firestore.collection(USER_COLLECTION).document(auth.uid!!)
                       .update("image", imageBase64).await()

                   getUserInfo()
                   _updateImage.emit(Resource.Success(Unit))

               }catch (e: Exception){
                    _updateImage.emit(Resource.Error("Falha ao atualizar informações!"))
               }
           }
        }
    }

    fun logout(){
        auth.signOut()
    }



}