package com.example.hqshop.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.hqshop.repository.FirebaseCommonRepository
import com.example.hqshop.util.Constants.SHARED_PREFERENCES
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSharedPreferences(
        application: Application
    ) = application.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommonRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommonRepository(firestore, firebaseAuth)
}