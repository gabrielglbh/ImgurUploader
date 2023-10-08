package com.gabr.gabc.imguruploader.presentation.loginPage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
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
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getUserData(onSessionOK: (Account) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val userName = sharedPreferencesProvider.getPref().getString(Constants.ACCOUNT_NAME, null) ?: ""
            val res = repository.getUserData(userName)
            res.fold(
                ifLeft = { err ->
                    if (err is ImageManagerFailure.Unauthorized) {
                        refreshAccessToken(onSessionOK)
                    }
                },
                ifRight = {
                    onSessionOK(it)
                }
            )
            _isLoading.value = false
        }
    }

    fun refreshAccessToken(onSessionOK: (Account) -> Unit) {
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
                    getUserData(onSessionOK)
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