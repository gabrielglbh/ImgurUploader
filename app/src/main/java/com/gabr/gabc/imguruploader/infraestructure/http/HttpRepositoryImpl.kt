package com.gabr.gabc.imguruploader.infraestructure.http

import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.infraestructure.imageManager.ImageManagerCalls
import retrofit2.Retrofit
import javax.inject.Inject

class HttpRepositoryImpl @Inject constructor(
    private val retrofit: Retrofit
) : HttpRepository {
    override fun getImageManagerService(): ImageManagerCalls {
        return retrofit.create(ImageManagerCalls::class.java)
    }
}