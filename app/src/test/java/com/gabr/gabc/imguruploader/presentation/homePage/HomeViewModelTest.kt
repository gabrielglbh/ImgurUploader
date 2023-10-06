package com.gabr.gabc.imguruploader.presentation.homePage

import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
        val viewModel = HomeViewModel(mockAuth)
        viewModel.signOut()
        coVerify { mockAuth.signOut() }
    }
}