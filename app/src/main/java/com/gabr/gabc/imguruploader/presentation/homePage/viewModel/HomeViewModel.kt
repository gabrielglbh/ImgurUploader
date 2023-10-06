package com.gabr.gabc.imguruploader.presentation.homePage.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImgurImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val imageManagerRepository: ImageManagerRepository,
    private val contentResolverProvider: ContentResolverProvider,
) : ViewModel() {
    private val _formState = MutableStateFlow(ImgFormState())
    val formState: StateFlow<ImgFormState> = _formState.asStateFlow()

    val images = mutableStateListOf<ImgurImage>()

    var isLoading = mutableStateOf(false)
       private set

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
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            val formValue = _formState.value
            val res = imageManagerRepository.uploadImage(
                formValue.title,
                formValue.description,
                encodeImageToBase64()
            )
            res.fold(
                ifLeft = { onError(it.error) },
                ifRight = {
                    shouldShowDetails.value = false
                }
            )
            isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    private fun encodeImageToBase64(): String {
        val imageStream = contentResolverProvider.resolver().openInputStream(_formState.value.link)
        val selectedImage = BitmapFactory.decodeStream(imageStream)

        val width = 400
        val aspectRatio = selectedImage.width.toFloat() / selectedImage.height.toFloat()
        val resizeSelectedImage = Bitmap.createScaledBitmap(
            selectedImage, width, (width / aspectRatio).roundToInt(), false
        )

        val stream = ByteArrayOutputStream()
        resizeSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val b = stream.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}