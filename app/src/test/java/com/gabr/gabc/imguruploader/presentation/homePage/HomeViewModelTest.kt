package com.gabr.gabc.imguruploader.presentation.homePage

import android.net.Uri
import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun signOut_Successful() = runTest {
        mockkStatic(Uri::class)
        every { Uri.EMPTY } returns mockk()

        val mockAuth = mockk<AuthRepository> {
            coEvery { signOut() } answers {}
        }
        val mockImageManagerRepository = mockk<ImageManagerRepository>()
        val mockContentProvider = mockk<ContentResolverProvider>()

        val viewModel = HomeViewModel(mockAuth, mockImageManagerRepository, mockContentProvider)
        viewModel.signOut()
        coVerify { mockAuth.signOut() }
    }
}