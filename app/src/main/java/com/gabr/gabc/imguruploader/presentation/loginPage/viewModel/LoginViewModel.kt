package com.gabr.gabc.imguruploader.presentation.loginPage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.presentation.shared.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: ImageManagerRepository,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
) : ViewModel() {
    fun getUserData(onSessionOK: (Account) -> Unit) {
        viewModelScope.launch {
            val userName = sharedPreferencesProvider.getPref().getString(Constants.ACCOUNT_NAME, null) ?: ""
            val res = repository.getUserData(userName)
            res.fold(
                ifLeft = {},
                ifRight = {
                    onSessionOK(it)
                }
            )
        }
    }

    fun refreshAccessToken() {
        viewModelScope.launch {
            val result = repository.getSession(
                sharedPreferencesProvider.getPref().getString(Constants.REFRESH_TOKEN, null) ?: "",
                Constants.CLIENT_ID,
                Constants.SECRET_CLIENT_ID,
            )
            result.fold(
                ifLeft = {},
                ifRight = {
                    saveTokens(it.accessToken, it.refreshToken, it.accountUsername)
                }
            )
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String, userName: String) {
        val p = sharedPreferencesProvider.getPref()
        with (p.edit()) {
            putString(Constants.ACCESS_TOKEN, accessToken)
            putString(Constants.REFRESH_TOKEN, refreshToken)
            putString(Constants.ACCOUNT_NAME, userName)
            apply()
        }
    }
}