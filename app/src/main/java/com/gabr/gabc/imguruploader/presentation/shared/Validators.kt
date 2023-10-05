package com.gabr.gabc.imguruploader.presentation.shared

import android.util.Patterns

class Validators {
    companion object {
        fun isEmailInvalid(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isPasswordInvalid(password: String) = password.trim().isEmpty() || password.length < 6
    }
}