package com.gabr.gabc.imguruploader.presentation.loginPage

import arrow.core.Either
import com.gabr.gabc.imguruploader.MainDispatcherRule
import com.gabr.gabc.imguruploader.domain.auth.AuthFailure
import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginFormState
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockUser = mockk<FirebaseUser>()
    private val mockAuthFailure = mockk<AuthFailure.SignInFailed> {
        every { error } returns "error"
    }

    @Test
    fun signInUser_Successful() = runTest {
        var result = false
        val mockAuth = mockk<AuthRepository> {
            coEvery { signInUser(any(), any()) } answers { Either.Right(mockUser) }
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
        val mockAuth = mockk<AuthRepository> {
            coEvery { signInUser(any(), any()) } answers { Either.Left(mockAuthFailure) }
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
        val mockAuth = mockk<AuthRepository> {
            coEvery { createUser(any(), any()) } answers { Either.Right(mockUser) }
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
        val mockAuth = mockk<AuthRepository> {
            coEvery { createUser(any(), any()) } answers { Either.Left(mockAuthFailure) }
        }
        val viewModel = LoginViewModel(mockAuth)
        viewModel.updateLoginState(LoginFormState("email", "pass"))
        viewModel.createUser {}
        Assert.assertTrue(viewModel.formState.value.error.isNotEmpty())
        Assert.assertTrue(viewModel.formState.value.password.isEmpty())
    }
}