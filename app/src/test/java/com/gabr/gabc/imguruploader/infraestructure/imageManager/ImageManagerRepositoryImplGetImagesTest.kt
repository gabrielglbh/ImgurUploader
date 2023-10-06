package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.net.Uri
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.CustomListResponse
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

class ImageManagerRepositoryImplGetImagesTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }

    @Test
    fun getImages_Successful() = runTest {
        // https://github.com/mockk/mockk/issues/344
        mockkStatic("retrofit2.KotlinExtensions")
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
        val mockJson = mockk<JSONObject> {
            every { this@mockk.get(any()) } returns ""
            every { getString("link") } returns "test"
            every { getString("type") } returns ""
            every { getString("deletehash") } returns ""
        }
        val mockCall = mockk<Call<CustomListResponse>> {
            coEvery { await() } coAnswers { CustomListResponse(
                data = listOf(mockJson),
                success = true,
                status = 200
            ) }
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getImages() } coAnswers { mockCall }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.getImages()
        Assert.assertTrue(user.isRight())
    }

    @Test
    fun getImages_Failure_404() = runTest {
        // https://github.com/mockk/mockk/issues/344
        mockkStatic("retrofit2.KotlinExtensions")
        val mockCall = mockk<Call<CustomListResponse>> {
            coEvery { await() } coAnswers { CustomListResponse(
                data = listOf(JSONObject()),
                success = false,
                status = 404
            ) }
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getImages() } coAnswers { mockCall }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.getImages()
        Assert.assertTrue(user.isLeft())
    }
}