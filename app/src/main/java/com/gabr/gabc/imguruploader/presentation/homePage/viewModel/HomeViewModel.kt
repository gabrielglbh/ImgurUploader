package com.gabr.gabc.imguruploader.presentation.homePage.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageManagerRepository: ImageManagerRepository,
    private val contentResolverProvider: ContentResolverProvider,
) : ViewModel() {
    private val _formState = MutableStateFlow(ImgFormState())
    val formState: StateFlow<ImgFormState> = _formState.asStateFlow()

    private val _userData = MutableStateFlow(Account())
    val userData: StateFlow<Account> = _userData.asStateFlow()

    private val _images = MutableStateFlow(mutableListOf<ImgurImage>())
    val images: StateFlow<MutableList<ImgurImage>> = _images.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasImageToUpload = MutableStateFlow(false)
    val hasImageToUpload: StateFlow<Boolean> = _hasImageToUpload.asStateFlow()

    private val _isDisplayingImageDetails = MutableStateFlow(false)
    val isDisplayingImageDetails: StateFlow<Boolean> = _isDisplayingImageDetails.asStateFlow()

    fun setForm(form: ImgFormState) {
        _formState.value = form
    }

    fun setUserData(account: Account) {
        _userData.value = account
    }

    fun setHasImageToUpload(uri: Uri?) {
        _hasImageToUpload.value = uri != null
        setForm(_formState.value.copy(link = uri))
        if (_isDisplayingImageDetails.value) {
            setIsDisplayingImageDetails(false)
        }
    }

    fun setIsDisplayingImageDetails(value: Boolean) {
        _isDisplayingImageDetails.value = value
        if (_hasImageToUpload.value) {
            setForm(ImgFormState())
        }
    }

    fun loadImages(onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = imageManagerRepository.getImages()
            res.fold(
                ifLeft = { onError(it.error) },
                ifRight = { list ->
                    val aux = mutableListOf<ImgurImage>()
                    with (aux) {
                        clear()
                        addAll(list)
                    }
                    _images.value = aux
                }
            )
            _isLoading.value = false
        }
    }

    fun deleteImage(deleteHash: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val userData = _userData.value
            val res = imageManagerRepository.deleteImage(
                userData.username,
                deleteHash
            )
            res.fold(
                ifLeft = { onError(it.error) },
                ifRight = {
                    onSuccess()
                    setIsDisplayingImageDetails(false)
                    loadImages(onError)
                }
            )
            _isLoading.value = false
        }
    }

    fun uploadImage(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val formValue = _formState.value
            val res = imageManagerRepository.uploadImage(
                formValue.title,
                formValue.description,
                imageToResizedTempFile()
            )
            res.fold(
                ifLeft = { onError(it.error) },
                ifRight = {
                    onSuccess()
                    setHasImageToUpload(null)
                    loadImages(onError)
                }
            )
            _isLoading.value = false
        }
    }

    private fun imageToResizedTempFile(): File {
        val fileName = Calendar.getInstance().timeInMillis.toString()
        val outputFile = File.createTempFile(fileName, ".jpeg", null)

        _formState.value.link?.let {
            contentResolverProvider.resolver().openInputStream(it)?.use { input ->
                val outputStream = FileOutputStream(outputFile)
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val byteCount = input.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                    output.close()
                }
            }
        }

        val bitmap = BitmapFactory.decodeFile(outputFile.path)

        val width = 1024
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap, width, (width / aspectRatio).roundToInt(), false
        )

        val resizedFile = File.createTempFile(fileName, ".jpeg", null)
        val fOut = FileOutputStream(resizedFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
        fOut.flush()
        fOut.close()

        return resizedFile
    }
}