package com.gabr.gabc.imguruploader.infraestructure.auth

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AuthRepositoryImplCreateUserTest {
    private val mockUser = mock<FirebaseUser> {}
    private val mockStringProvider = mock<StringResourcesProvider> {
        on { getString(any()) } doReturn ""
    }
    private val email = "email"
    private val password = "password"

    @Test
    fun createUser_Successful() = runTest {
        val mockAuthResult = mock<AuthResult> {
            on { user } doReturn mockUser
        }
        val mockTask = mock<Task<AuthResult>> {
            on { isComplete } doReturn true
            on { isSuccessful } doReturn true
        }
        val mockAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(email, password) } doAnswer { mockTask }
            onBlocking { mockTask.await() } doAnswer { mockAuthResult }
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.createUser(email, password)
        Assert.assertEquals(user.isRight(), true)
    }

    @Test
    fun createUser_Failure_WithUserNull() = runTest {
        val mockAuthResult = mock<AuthResult> {
            on { user } doReturn null
        }
        val mockTask = mock<Task<AuthResult>> {
            on { isComplete } doReturn true
            on { isSuccessful } doReturn true
        }
        val mockAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(email, password) } doAnswer { mockTask }
            onBlocking { mockTask.await() } doAnswer { mockAuthResult }
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.createUser(email, password)
        Assert.assertEquals(user.isLeft(), true)
    }

    @Test
    fun createUser_Failure_WithFirebaseAuthException() = runTest {
        val mockFirebaseAuthException = mock<FirebaseAuthException> {}
        val mockTask = mock<Task<AuthResult>> {
            on { isComplete } doReturn true
            on { isSuccessful } doReturn false
            on { exception } doReturn mockFirebaseAuthException
        }
        val mockAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(email, password) } doAnswer { mockTask }
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.createUser(email, password)
        Assert.assertEquals(user.isLeft(), true)
    }

    @Test
    fun createUser_Failure_WithIllegalArgumentException() = runTest {
        val mockIllegalArgumentException = mock<IllegalArgumentException> {}
        val mockTask = mock<Task<AuthResult>> {
            on { isComplete } doReturn true
            on { isSuccessful } doReturn false
            on { exception } doReturn mockIllegalArgumentException
        }
        val mockAuth = mock<FirebaseAuth> {
            on { createUserWithEmailAndPassword(email, password) } doAnswer { mockTask }
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.createUser(email, password)
        Assert.assertEquals(user.isLeft(), true)
    }
}