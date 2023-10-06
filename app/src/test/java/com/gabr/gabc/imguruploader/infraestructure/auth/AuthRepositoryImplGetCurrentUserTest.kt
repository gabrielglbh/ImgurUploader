package com.gabr.gabc.imguruploader.infraestructure.auth

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthRepositoryImplGetCurrentUserTest {
    private val mockUser = mockk<FirebaseUser>()
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }

    @Test
    fun currentUserIs_Null() = runTest {
        val mockAuth = mockk<FirebaseAuth> {
            every { currentUser } returns null
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.getCurrentUser()
        assertEquals(user, null)
    }

    @Test
    fun currentUserIs_User() = runTest {
        val mockAuth = mockk<FirebaseAuth> {
            every { currentUser } returns mockUser
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.getCurrentUser()
        assertEquals(user, mockUser)
    }
}