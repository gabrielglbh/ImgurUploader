package com.gabr.gabc.imguruploader.presentation.splashPage

import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.presentation.splashPage.viewModel.SplashViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class SplashViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockUser = mock<FirebaseUser> {}

    @Test
    fun checkIfUserIsSignedIn_Successful() = runTest {
        var result = false
        val mockAuth = mock<AuthRepository> {
            onBlocking { getCurrentUser() } doAnswer { mockUser }
        }
        val viewModel = SplashViewModel(mockAuth)
        viewModel.checkIfUserIsSignedIn(
            ifUserExists = { result = true },
            ifUserDoesNotExist = {},
        )
        Assert.assertEquals(result, true)
    }

    @Test
    fun checkIfUserIsSignedIn_Failure() = runTest {
        var result = false
        val mockAuth = mock<AuthRepository> {
            onBlocking { getCurrentUser() } doAnswer { null }
        }
        val viewModel = SplashViewModel(mockAuth)
        viewModel.checkIfUserIsSignedIn(
            ifUserExists = {},
            ifUserDoesNotExist = { result = true },
        )
        Assert.assertEquals(result, true)
    }
}