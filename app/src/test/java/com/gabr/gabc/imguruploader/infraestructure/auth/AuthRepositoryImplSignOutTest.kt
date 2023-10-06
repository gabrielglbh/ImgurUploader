package com.gabr.gabc.imguruploader.infraestructure.auth

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthRepositoryImplSignOutTest {
    private val mockStringProvider = mockk<StringResourcesProvider>()

    @Test
    fun signOut_Successful() = runTest {
        val mockAuth = mockk<FirebaseAuth> {
            every { signOut() } answers {}
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        repositoryImpl.signOut()
        verify { mockAuth.signOut() }
    }
}