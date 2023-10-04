package com.gabr.gabc.imguruploader.infraestructure.auth

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AuthRepositoryImplGetCurrentUserTest {
    private val mockUser = mock<FirebaseUser> {}
    private val mockStringProvider = mock<StringResourcesProvider> {
        on { getString(any()) } doReturn ""
    }

    @Test
    fun currentUserIs_Null() = runTest {
        val mockAuth = mock<FirebaseAuth> {
            on { currentUser } doReturn null
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.getCurrentUser()
        assertEquals(user, null)
    }

    @Test
    fun currentUserIs_User() = runTest {
        val mockAuth = mock<FirebaseAuth> {
            on { currentUser } doReturn mockUser
        }
        val repositoryImpl = AuthRepositoryImpl(mockAuth, mockStringProvider)
        val user = repositoryImpl.getCurrentUser()
        assertEquals(user, mockUser)
    }
}