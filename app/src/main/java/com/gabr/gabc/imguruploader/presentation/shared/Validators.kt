package com.gabr.gabc.imguruploader.presentation.shared

import android.util.Patterns

class Validators {
    companion object {
        fun isEmailInvalid(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isPasswordInvalid(password: String) = password.trim().isEmpty() || password.length < 6
        fun isTitleInvalid(title: String) = title.length > 24
        fun isDescriptionInvalid(desc: String) = desc.length > 128
    }
}