package com.gabr.gabc.imguruploader.presentation.homePage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Either
import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
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

    private val mockContentProvider = mockk<ContentResolverProvider>()
    private val mockBitmap = mockk<Bitmap> {
        every { width } returns 400
        every { height } returns 400
        every { compress(any(), any(), any()) } returns true
    }

    @Test
    fun uploadImage_Successful() = runTest {
        mockkStatic(Bitmap::class)
        mockkStatic(BitmapFactory::class)

        every { BitmapFactory.decodeFile(any()) } returns mockBitmap
        every { Bitmap.createScaledBitmap(any(), any(), any(), any()) } returns mockBitmap

        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { uploadImage(any(), any(), any()) } answers { Either.Right(mockk()) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.uploadImage {}
        coVerify { mockImageManagerRepository.uploadImage(any(), any(), any()) }
        verify { mockBitmap.compress(any(), any(), any()) }
    }

    @Test
    fun uploadImage_Failure() = runTest {
        mockkStatic(Bitmap::class)
        mockkStatic(BitmapFactory::class)

        var result = false
        every { BitmapFactory.decodeFile(any()) } returns mockBitmap
        every { Bitmap.createScaledBitmap(any(), any(), any(), any()) } returns mockBitmap

        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { uploadImage(any(), any(), any()) } answers { Either.Left(mockk()) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.uploadImage { result = true }
        Assert.assertTrue(result)
    }

    @Test
    fun loadImages_Successful() = runTest {
        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { getImages() } answers { Either.Right(listOf<ImgurImage>(mockk(), mockk())) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.loadImages {}
        Assert.assertEquals(viewModel.images.size, 2)
    }

    @Test
    fun loadImages_Failure() = runTest {
        var result = false
        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { getImages() } answers { Either.Left(mockk()) }
        }

        val viewModel = HomeViewModel(mockImageManagerRepository, mockContentProvider)
        viewModel.loadImages { result = true }
        Assert.assertTrue(result)
    }
}