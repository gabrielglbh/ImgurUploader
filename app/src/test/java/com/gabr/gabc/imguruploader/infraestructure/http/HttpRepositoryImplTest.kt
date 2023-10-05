package com.gabr.gabc.imguruploader.infraestructure.http

import com.gabr.gabc.imguruploader.infraestructure.imageManager.ImageManagerCalls
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit

class HttpRepositoryImplTest {
    @Test
    fun getImageManagerService_Successful() {
        val mockImageManagerCalls = mockk<ImageManagerCalls>()
        val mockRetrofit = mockk<Retrofit> {
            every { create(ImageManagerCalls::class.java) } returns mockImageManagerCalls
        }

        val result = HttpRepositoryImpl(mockRetrofit).getImageManagerService()
        Assert.assertNotNull(result)
        Assert.assertEquals(mockImageManagerCalls, result)
    }
}