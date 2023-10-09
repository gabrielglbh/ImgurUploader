package com.gabr.gabc.imguruploader.presentation.homePage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.Either
import com.gabr.gabc.imguruploader.presentation.MainDispatcherRule
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.ImgFormState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mockImageManagerFailure = mockk<ImageManagerFailure> {
        every { error } returns ""
    }
    private val mockContentProvider = mockk<ContentResolverProvider>()
    private val mockBitmap = mockk<Bitmap> {
        every { width } returns 400
        every { height } returns 400
        every { compress(any(), any(), any()) } returns true
    }

    @Test
    fun setForm_Successful() {
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        Assert.assertEquals(viewModel.formState.value.title, "")
        Assert.assertEquals(viewModel.formState.value.description, "")
        viewModel.setForm(ImgFormState(title = "test", description = "test"))
        Assert.assertEquals(viewModel.formState.value.title, "test")
        Assert.assertEquals(viewModel.formState.value.description, "test")
    }

    @Test
    fun setUserData_Successful() {
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        Assert.assertEquals(viewModel.userData.value?.username, "")
        viewModel.setUserData(Account(username = "test"))
        Assert.assertEquals(viewModel.userData.value?.username, "test")
    }

    @Test
    fun setHasImageToUpload_When_DetailsNotShown() {
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        val mockUri = mockk<Uri>()
        viewModel.setHasImageToUpload(mockUri)
        Assert.assertTrue(viewModel.hasImageToUpload.value == true)
        Assert.assertEquals(viewModel.formState.value.link, mockUri)
    }

    @Test
    fun setHasImageToUpload_When_DetailsIsShown() {
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        val mockUri = mockk<Uri>()
        viewModel.setIsDisplayingImageDetails(true)
        viewModel.setHasImageToUpload(mockUri)
        Assert.assertTrue(viewModel.hasImageToUpload.value == true)
        Assert.assertTrue(viewModel.isDisplayingImageDetails.value == false)
    }

    @Test
    fun setIsDisplayingImageDetails_When_FormNotShown() {
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.setIsDisplayingImageDetails(true)
        Assert.assertTrue(viewModel.isDisplayingImageDetails.value == true)
    }

    @Test
    fun setIsDisplayingImageDetails_When_FormIsShown() {
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        val mockUri = mockk<Uri>()
        viewModel.setHasImageToUpload(mockUri)
        viewModel.setIsDisplayingImageDetails(true)
        Assert.assertTrue(viewModel.isDisplayingImageDetails.value == true)
        Assert.assertEquals(viewModel.formState.value, ImgFormState())
    }

    @Test
    fun uploadImage_Successful() = runTest {
        mockkStatic(Bitmap::class)
        mockkStatic(BitmapFactory::class)

        every { BitmapFactory.decodeFile(any()) } returns mockBitmap
        every { Bitmap.createScaledBitmap(any(), any(), any(), any()) } returns mockBitmap

        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { uploadImage(any(), any(), any()) } answers { Either.Right(mockk()) }
            coEvery { getImages() } answers { Either.Right(listOf(mockk())) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.uploadImage(onSuccess = {}, onError = {})
        coVerify { mockImageManagerRepository.uploadImage(any(), any(), any()) }
        verify { mockBitmap.compress(any(), any(), any()) }
        coVerify { mockImageManagerRepository.getImages() }
    }

    @Test
    fun uploadImage_Failure() = runTest {
        mockkStatic(Bitmap::class)
        mockkStatic(BitmapFactory::class)

        every { BitmapFactory.decodeFile(any()) } returns mockBitmap
        every { Bitmap.createScaledBitmap(any(), any(), any(), any()) } returns mockBitmap

        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { uploadImage(any(), any(), any()) } answers { Either.Left(mockImageManagerFailure) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.uploadImage(onSuccess = {}, onError = {})
        coVerify(exactly = 0) { mockImageManagerRepository.getImages() }
    }

    @Test
    fun loadImages_Successful() = runTest {
        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { getImages() } answers { Either.Right(listOf(mockk(), mockk())) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.loadImages {}
        Assert.assertEquals(viewModel.images.value?.size, 2)
    }

    @Test
    fun loadImages_Failure() = runTest {
        var result = false
        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { getImages() } answers { Either.Left(mockImageManagerFailure) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.loadImages { result = true }
        Assert.assertTrue(result)
    }

    @Test
    fun deleteImage_Successful() = runTest {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()

        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { getImages() } answers { Either.Right(listOf(mockk())) }
            coEvery { deleteImage(any(), any()) } answers { Either.Right(Unit) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.setUserData(Account("devgglop", Uri.parse("")))
        viewModel.deleteImage("", {}, {})
        coVerify { mockImageManagerRepository.getImages() }
    }

    @Test
    fun deleteImage_Failure() = runTest {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { deleteImage(any(), any()) } answers { Either.Left(mockImageManagerFailure) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.setUserData(Account("devgglop", Uri.parse("")))
        viewModel.deleteImage("", {}, {})
        coVerify(exactly = 0) { mockImageManagerRepository.getImages() }
    }
}