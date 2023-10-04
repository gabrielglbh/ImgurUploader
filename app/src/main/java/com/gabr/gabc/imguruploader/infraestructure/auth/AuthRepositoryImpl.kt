package com.gabr.gabc.imguruploader.infraestructure.auth

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.Either.Left
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.auth.AuthFailure
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val res: StringResourcesProvider,
    ) : AuthRepository {
    override suspend fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun signInUser(
        email: String,
        password: String
    ): Either<AuthFailure, FirebaseUser> {
        val signInFailed = res.getString(R.string.error_sign_in)
        try {
            auth.signInWithEmailAndPassword(email, password).await().user?.let {
                return Right(it)
            }
            return Left(AuthFailure.SignInFailed(signInFailed))
        } catch (err: FirebaseAuthException) {
            return Left(AuthFailure.SignInFailed(signInFailed))
        } catch (err: IllegalArgumentException) {
            return Left(AuthFailure.SignInFailed(res.getString(R.string.error_empty_form)))
        }
    }

    override suspend fun createUser(
        email: String,
        password: String
    ): Either<AuthFailure, FirebaseUser> {
        val signInFailed = res.getString(R.string.error_register)
        try {
            auth.createUserWithEmailAndPassword(email, password).await().user?.let {
                return Right(it)
            }
            return Left(AuthFailure.UserCreationFailed(signInFailed))
        } catch (err: FirebaseAuthException) {
            return Left(AuthFailure.UserCreationFailed(signInFailed))
        } catch (err: IllegalArgumentException) {
            return Left(AuthFailure.UserCreationFailed(res.getString(R.string.error_empty_form)))
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}