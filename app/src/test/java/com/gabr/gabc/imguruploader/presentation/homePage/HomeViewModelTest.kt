package com.gabr.gabc.imguruploader.presentation.homePage

import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun signOut_Successful() = runTest {
        val mockAuth = mock<AuthRepository> {
            onBlocking { signOut() } doAnswer {}
        }
        val viewModel = HomeViewModel(mockAuth)
        viewModel.signOut()
        verify(mockAuth).signOut()
    }
}