package com.gabr.gabc.imguruploader.presentation.loginPage

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import arrow.core.Either
import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.OAuth
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun getUserData_Successful() = runTest {
        var result = false
        val mockSP = mockk<SharedPreferences> {
            every { getString(any(), any()) } returns ""
        }
        val mockSharedPreferences = mockk<SharedPreferencesProvider> {
            every { getPref() } returns mockSP
        }
        val mockImageManager = mockk<ImageManagerRepository> {
            coEvery { getUserData(any()) } answers { Either.Right(mockk()) }
        }
        val viewModel = LoginViewModel(mockImageManager, mockSharedPreferences)
        viewModel.getUserData {
            result = true
        }
        Assert.assertTrue(result)
    }

    @Test
    fun getUserData_Failure_Generic() = runTest {
        var result = false
        val mockSP = mockk<SharedPreferences> {
            every { getString(any(), any()) } returns ""
        }
        val mockSharedPreferences = mockk<SharedPreferencesProvider> {
            every { getPref() } returns mockSP
        }
        val mockImageManager = mockk<ImageManagerRepository> {
            coEvery { getUserData(any()) } answers { Either.Left(mockk()) }
        }
        val viewModel = LoginViewModel(mockImageManager, mockSharedPreferences)
        viewModel.getUserData {
            result = true
        }
        Assert.assertTrue(!result)
    }

    @Test
    fun getUserData_Failure_Unauthorized() = runTest {
        val mockUnauthorized = mockk<ImageManagerFailure.Unauthorized>()
        var result = false
        val mockSP = mockk<SharedPreferences> {
            every { getString(any(), any()) } returns ""
        }
        val mockSharedPreferences = mockk<SharedPreferencesProvider> {
            every { getPref() } returns mockSP
        }
        val mockImageManager = mockk<ImageManagerRepository> {
            coEvery { getUserData(any()) } answers { Either.Left(mockUnauthorized) }
            coEvery { getSession(any(), any(), any(), any()) } answers { Either.Left(mockk()) }
        }
        val viewModel = LoginViewModel(mockImageManager, mockSharedPreferences)
        viewModel.getUserData {
            result = true
        }
        Assert.assertTrue(!result)
        coVerify { mockImageManager.getSession(any(), any(), any()) }
    }

    @Test
    fun refreshAccessToken_Successful() = runTest {
        val mockOAuth = mockk<OAuth> {
            every { accessToken } returns ""
            every { refreshToken } returns ""
            every { accountUsername } returns ""
        }
        val mockSPEditor = mockk<Editor> {
            every { putString(any(), any()) } returns mockk()
            every { apply() } returns mockk()
        }
        val mockSP = mockk<SharedPreferences> {
            every { getString(any(), any()) } returns ""
            every { edit() } returns mockSPEditor
        }
        val mockSharedPreferences = mockk<SharedPreferencesProvider> {
            every { getPref() } returns mockSP
        }
        val mockImageManager = mockk<ImageManagerRepository> {
            coEvery { getSession(any(), any(), any(), any()) } answers { Either.Right(mockOAuth) }
        }
        val viewModel = LoginViewModel(mockImageManager, mockSharedPreferences)
        viewModel.refreshAccessToken {}
        verify(exactly = 2) { mockSharedPreferences.getPref() }
    }
}