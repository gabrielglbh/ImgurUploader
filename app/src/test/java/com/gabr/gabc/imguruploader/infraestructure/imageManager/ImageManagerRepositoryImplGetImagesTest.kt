package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.content.SharedPreferences
import android.net.Uri
import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurImageDto
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import retrofit2.Response

class ImageManagerRepositoryImplGetImagesTest {
    private val mockStringProvider = mockk<StringResourcesProvider> {
        every { getString(any()) } returns ""
    }
    private val mockSP = mockk<SharedPreferences> {
        every { getString(any(), any()) } returns ""
    }
    private val mockSharedPreferences = mockk<SharedPreferencesProvider> {
        every { getPref() } returns mockSP
    }

    @Test
    fun getImages_Successful() = runTest {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
        val dto = ImgurImageDto("", "", "")
        val mockImgurResponse = mockk<ImgurResponse<List<ImgurImageDto>>> {
            every { data } returns listOf(dto)
        }
        val mockResponse = mockk<Response<ImgurResponse<List<ImgurImageDto>>>> {
            every { isSuccessful } returns true
            every { body() } returns mockImgurResponse
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getImages(any()) } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider, mockSharedPreferences)
        val user = repositoryImpl.getImages()
        Assert.assertTrue(user.isRight())
    }

    @Test
    fun getImages_Failure_404() = runTest {
        val mockResponse = mockk<Response<ImgurResponse<List<ImgurImageDto>>>> {
            every { isSuccessful } returns false
        }
        val mockImageManagerCalls = mockk<ImageManagerCalls> {
            coEvery { getImages(any()) } coAnswers { mockResponse }
        }
        val mockHttp = mockk<HttpRepository> {
            every { getImageManagerService() } returns mockImageManagerCalls
        }

        val repositoryImpl = ImageManagerRepositoryImpl(mockHttp, mockStringProvider, mockSharedPreferences)
        val user = repositoryImpl.getImages()
        Assert.assertTrue(user.isLeft())
    }
}