package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
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
        val mockJsonElement = mockk<JsonElement> {
            every { asJsonObject } returns mockk()
        }
        val mockJsonParser = mockk<JsonParser> {
            every { parse(any<String>()) } returns mockJsonElement
        }
        val mockResponseBody = mockk<ResponseBody> {
            every { string() } returns "{'data': {'test': 'test'}}"
        }
        val mockResponse = mockk<Response<ResponseBody>> {
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
        val mockResponse = mockk<Response<ResponseBody>> {
            every { isSuccessful } returns false
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