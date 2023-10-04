package com.gabr.gabc.imguruploader.presentation.splashPage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    fun checkIfUserIsSignedIn(ifUserExists: () -> Unit, ifUserDoesNotExist: () -> Unit) {
        viewModelScope.launch {
            if (repository.getCurrentUser() != null) {
                ifUserExists()
            } else {
                ifUserDoesNotExist()
            }
        }
    }
}