package com.gabr.gabc.imguruploader.presentation.homePage.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImgurImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val imageManagerRepository: ImageManagerRepository,
) : ViewModel() {
    private val _formState = MutableStateFlow(ImgFormState())
    val formState: StateFlow<ImgFormState> = _formState.asStateFlow()

    val images = mutableStateListOf<ImgurImage>()

    var shouldShowDetails = mutableStateOf(false)
        private set

    fun updateShouldShowFormDialog(value: Boolean, uri: Uri = Uri.EMPTY) {
        shouldShowDetails.value = value
        updateForm(_formState.value.copy(link = uri))
    }

    fun updateForm(form: ImgFormState) {
        _formState.value = form
    }

    fun uploadImage(onError: (String) -> Unit) {
        viewModelScope.launch {
            val formValue = _formState.value
            val res = imageManagerRepository.uploadImage(
                ImgurImage(
                    title = formValue.title,
                    description = formValue.description,
                    link = formValue.link,
                )
            )
            res.fold(
                ifLeft = { onError(it.error) },
                ifRight = {
                    shouldShowDetails.value = false
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}