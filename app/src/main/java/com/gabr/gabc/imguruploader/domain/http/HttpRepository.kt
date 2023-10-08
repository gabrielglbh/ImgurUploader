package com.gabr.gabc.imguruploader.domain.http

import com.gabr.gabc.imguruploader.infraestructure.imageManager.ImageManagerCalls

interface HttpRepository {
    fun getImageManagerService(): ImageManagerCalls
}