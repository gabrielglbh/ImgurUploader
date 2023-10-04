package com.gabr.gabc.imguruploader.domain.auth

import arrow.core.Either
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Either<AuthFailure, FirebaseUser>
    suspend fun createUser(email: String, password: String): Either<AuthFailure, FirebaseUser>
    suspend fun signOut()
}