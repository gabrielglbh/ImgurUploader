package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.net.Uri
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImgurImage
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

class ImageManagerRepositoryImplDeleteImageTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }

    @Test
    fun deleteImage_Successful() = runTest {
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
            coEvery { deleteImage(any(), any()) } coAnswers { mockCall }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.deleteImage("", "")
        Assert.assertTrue(user.isRight())
    }

    @Test
    fun deleteImage_Failure_404() = runTest {
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
            coEvery { deleteImage(any(), any()) } coAnswers { mockCall }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.deleteImage("", "")
        Assert.assertTrue(user.isLeft())
    }
}