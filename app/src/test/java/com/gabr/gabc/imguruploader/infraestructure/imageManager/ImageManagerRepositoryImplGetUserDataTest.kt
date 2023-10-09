package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.AccountDto
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import retrofit2.Response

class ImageManagerRepositoryImplGetUserDataTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }
    private val mockSharedPreferences = mockk<SharedPreferencesProvider>()

    @Test
    fun getUserName_Successful() = runTest {
        val mockResponseBody = mockk<ImgurResponse<AccountDto>> {
            every { data } returns AccountDto("", "")
        }
        val mockResponse = mockk<Response<ImgurResponse<AccountDto>>> {
            every { isSuccessful } returns true
            every { body() } returns mockResponseBody
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getUserData(any()) } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider, mockSharedPreferences)
        val user = repositoryImpl.getUserData("")
        Assert.assertTrue(user.isRight())
    }

    @Test
    fun getUserName_Failure_404() = runTest {
        val mockResponse = mockk<Response<ImgurResponse<AccountDto>>> {
            every { isSuccessful } returns false
            every { code() } returns 404
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getUserData(any()) } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider, mockSharedPreferences)
        val user = repositoryImpl.getUserData("")
        Assert.assertTrue(user.isLeft())
    }
}