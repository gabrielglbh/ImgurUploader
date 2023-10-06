package com.gabr.gabc.imguruploader.presentation.homePage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Either
import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun signOut_Successful() = runTest {
        val mockAuth = mockk<AuthRepository> {
            coEvery { signOut() } answers {}
        }
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val mockContentProvider = mockk<ContentResolverProvider>()

        val viewModel = HomeViewModel(mockAuth, mockImageManagerRepository, mockContentProvider)
        viewModel.signOut()
        coVerify { mockAuth.signOut() }
    }

    @Test
    fun uploadImage_Successful() = runTest {
        mockkStatic(Bitmap::class)
        mockkStatic(BitmapFactory::class)

        val mockBitmap = mockk<Bitmap> {
            every { width } returns 400
            every { height } returns 400
            every { compress(any(), any(), any()) } returns true
        }
        every { BitmapFactory.decodeFile(any()) } returns mockBitmap
        every { Bitmap.createScaledBitmap(any(), any(), any(), any()) } returns mockBitmap

        val mockEither = mockk<Either<ImageManagerFailure, Unit>> {
            every { fold<Unit>(any(), any()) } answers { mockk() }
        }
        val mockAuth = mockk<AuthRepository> {
            coEvery { signOut() } answers {}
        }
        val mockImageManagerRepository = mockk<ImageManagerRepository> {
            coEvery { uploadImage(any(), any(), any()) } answers { mockEither }
        }
        val mockContentProvider = mockk<ContentResolverProvider>()

        val viewModel = HomeViewModel(mockAuth, mockImageManagerRepository, mockContentProvider)
        viewModel.uploadImage {}
        coVerify { mockImageManagerRepository.uploadImage(any(), any(), any()) }
        verify { mockBitmap.compress(any(), any(), any()) }
    }
}