package com.gabr.gabc.imguruploader.domain.imageManager

import arrow.core.Either
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.domain.imageManager.models.OAuth
import retrofit2.http.Part
import java.io.File

interface ImageManagerRepository {
    suspend fun getSession(
        refreshToken: String,
        clientId: String,
        clientSecret: String,
        clientType: String = "refresh_token",
    ): Either<ImageManagerFailure, OAuth>
    suspend fun getUserData(userName: String): Either<ImageManagerFailure, Account>
    suspend fun uploadImage(title: String, description: String, file: File): Either<ImageManagerFailure, Unit>
    suspend fun deleteImage(userName: String, deleteHash: String): Either<ImageManagerFailure, Unit>
    suspend fun getImages(): Either<ImageManagerFailure, List<ImgurImage>>
}