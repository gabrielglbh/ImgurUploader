package com.gabr.gabc.imguruploader.infraestructure.auth

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class AuthRepositoryImplSignInUserTest {
    private val mockUser = mockk<FirebaseUser>()
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }
    private val email = "email"
    private val password = "password"

    @Test
    fun signInUser_Successful() = runTest {
        val mockAuthResult = mockk<AuthResult> {
            every { user } returns mockUser
        }
        val mockTask = mockk<Task<AuthResult>> {
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { isCanceled } returns false
            every { result } returns mockAuthResult
            every { exception } returns null
        }
        val mockAuth = mockk<FirebaseAuth> {
            every { signInWithEmailAndPassword(email, password) } answers { mockTask }
        }

        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.signInUser(email, password)
        Assert.assertEquals(user.isRight(), true)
    }

    @Test
    fun signInUser_Failure_WithUserNull() = runTest {
        val mockAuthResult = mockk<AuthResult> {
            every { user } returns null
        }
        val mockTask = mockk<Task<AuthResult>> {
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { isCanceled } returns false
            every { result } returns mockAuthResult
            every { exception } returns null
        }
        val mockAuth = mockk<FirebaseAuth> {
            every { signInWithEmailAndPassword(email, password) } answers { mockTask }
        }

        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.signInUser(email, password)
        Assert.assertEquals(user.isLeft(), true)
    }

    @Test
    fun signInUser_Failure_WithFirebaseAuthException() = runTest {
        val mockFirebaseAuthException = mockk<FirebaseAuthException> {}
        val mockTask = mockk<Task<AuthResult>> {
            every { isComplete } returns true
            every { isSuccessful } returns false
            every { isCanceled } returns false
            every { result } returns null
            every { exception } returns mockFirebaseAuthException
        }
        val mockAuth = mockk<FirebaseAuth> {
            every { signInWithEmailAndPassword(email, password) } answers { mockTask }
        }

        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.signInUser(email, password)
        Assert.assertEquals(user.isLeft(), true)
    }

    @Test
    fun signInUser_Failure_WithIllegalArgumentException() = runTest {
        val mockIllegalArgumentException = mockk<IllegalArgumentException> {}
        val mockTask = mockk<Task<AuthResult>> {
            every { isComplete } returns true
            every { isSuccessful } returns false
            every { isCanceled } returns false
            every { result } returns null
            every { exception } returns mockIllegalArgumentException
        }
        val mockAuth = mockk<FirebaseAuth> {
            every { signInWithEmailAndPassword(email, password) } answers { mockTask }
        }

        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.signInUser(email, password)
        Assert.assertEquals(user.isLeft(), true)
    }
}