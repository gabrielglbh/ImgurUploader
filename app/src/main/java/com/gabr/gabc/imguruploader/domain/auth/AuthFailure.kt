package com.gabr.gabc.imguruploader.domain.auth

sealed class AuthFailure(open val error: String) {
    data class SignInFailed(override val error: String) : AuthFailure(error)
    data class UserCreationFailed(override val error: String) : AuthFailure(error)
    data class UserDoesNotExist(override val error: String) : AuthFailure(error)
}
