package com.gabr.gabc.imguruploader.presentation.loginPage

import arrow.core.Either
import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.domain.auth.AuthFailure
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginFormState
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockUser = mock<FirebaseUser> {}
    private val mockAuthFailure = mock<AuthFailure.SignInFailed> {
        on { error } doReturn "error"
    }

    @Test
    fun signInUser_Successful() = runTest {
        var result = false
        val mockAuth = mock<AuthRepository> {
            onBlocking { signInUser(any(), any()) } doAnswer { Either.Right(mockUser) }
        }
        val viewModel = LoginViewModel(mockAuth)
        viewModel.updateLoginState(LoginFormState("email", "pass"))
        viewModel.signInUser {
            result = true
        }
        Assert.assertTrue(result)
    }

    @Test
    fun signInUser_Failure() = runTest {
        val mockAuth = mock<AuthRepository> {
            onBlocking { signInUser(any(), any()) } doAnswer { Either.Left(mockAuthFailure) }
        }
        val viewModel = LoginViewModel(mockAuth)
        viewModel.updateLoginState(LoginFormState("email", "pass"))
        viewModel.signInUser {}
        Assert.assertTrue(viewModel.formState.value.error.isNotEmpty())
        Assert.assertTrue(viewModel.formState.value.password.isEmpty())
    }

    @Test
    fun createUser_Successful() = runTest {
        var result = false
        val mockAuth = mock<AuthRepository> {
            onBlocking { createUser(any(), any()) } doAnswer { Either.Right(mockUser) }
        }
        val viewModel = LoginViewModel(mockAuth)
        viewModel.updateLoginState(LoginFormState("email", "pass"))
        viewModel.createUser {
            result = true
        }
        Assert.assertTrue(result)
    }

    @Test
    fun createUser_Failure() = runTest {
        val mockAuth = mock<AuthRepository> {
            onBlocking { createUser(any(), any()) } doAnswer { Either.Left(mockAuthFailure) }
        }
        val viewModel = LoginViewModel(mockAuth)
        viewModel.updateLoginState(LoginFormState("email", "pass"))
        viewModel.createUser {}
        Assert.assertTrue(viewModel.formState.value.error.isNotEmpty())
        Assert.assertTrue(viewModel.formState.value.password.isEmpty())
    }
}