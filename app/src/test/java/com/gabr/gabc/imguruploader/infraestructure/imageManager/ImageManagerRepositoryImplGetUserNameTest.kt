package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.CustomResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import retrofit2.Call
import retrofit2.await

class ImageManagerRepositoryImplGetUserNameTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }

    @Test
    fun getUserName_Successful() = runTest {
        // https://github.com/mockk/mockk/issues/344
        mockkStatic("retrofit2.KotlinExtensions")
        val mockCall = mockk<Call<CustomResponse>> {
            coEvery { await() } coAnswers { CustomResponse(
                data = JSONObject(),
                success = true,
                status = 200
            ) }
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getUserName() } coAnswers { mockCall }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.getUserName()
        Assert.assertEquals(user.isRight(), true)
    }

    @Test
    fun getUserName_Failure_404() = runTest {
        // https://github.com/mockk/mockk/issues/344
        mockkStatic("retrofit2.KotlinExtensions")
        val mockCall = mockk<Call<CustomResponse>> {
            coEvery { await() } coAnswers { CustomResponse(
                data = JSONObject(),
                success = false,
                status = 404
            ) }
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getUserName() } coAnswers { mockCall }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.getUserName()
        Assert.assertEquals(user.isLeft(), true)
    }
}