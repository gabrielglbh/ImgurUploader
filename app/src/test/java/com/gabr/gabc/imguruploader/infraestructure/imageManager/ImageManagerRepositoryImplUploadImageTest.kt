package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.net.Uri
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import retrofit2.Response

class ImageManagerRepositoryImplUploadImageTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }

    @Test
    fun uploadImage_Successful() = runTest {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()

        val dto = ImgurImageDto("", "", "")
        val mockResponse = mockk<Response<ImgurImageDto>> {
            every { isSuccessful } returns true
            every { body() } returns dto
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { uploadImage(any()) } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.uploadImage("", "", "")
        Assert.assertTrue(user.isRight())
    }

    @Test
    fun uploadImage_Failure_404() = runTest {
        val mockResponse = mockk<Response<ImgurImageDto>> {
            every { isSuccessful } returns false
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { uploadImage(any()) } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider)
        val user = repositoryImpl.uploadImage("", "", "")
        Assert.assertTrue(user.isLeft())
    }
}