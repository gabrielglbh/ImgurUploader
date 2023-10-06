package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import retrofit2.Response

class ImageManagerRepositoryImplGetUserNameTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }

    @Test
    fun getUserName_Successful() = runTest {
        val dto = JSONObject()
        dto.put("account_url", "test")
        val mockResponse = mockk<Response<JSONObject>> {
            every { isSuccessful } returns true
            every { body() } returns dto
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getUserName() } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.getUserName()
        Assert.assertTrue(user.isRight())
    }

    @Test
    fun getUserName_Failure_404() = runTest {
        val mockResponse = mockk<Response<JSONObject>> {
            every { isSuccessful } returns false
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getUserName() } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.getUserName()
        Assert.assertTrue(user.isLeft())
    }
}