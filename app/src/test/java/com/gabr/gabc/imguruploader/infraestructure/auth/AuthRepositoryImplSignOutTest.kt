package com.gabr.gabc.imguruploader.infraestructure.auth

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AuthRepositoryImplSignOutTest {
    private val mockStringProvider = mock<StringResourcesProvider> {}

    @Test
    fun signOut_Successful() = runTest {
        val mockAuth = mock<FirebaseAuth> {
            on { signOut() } doAnswer { null }
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        repositoryImpl.signOut()
        verify(mockAuth).signOut()
    }
}