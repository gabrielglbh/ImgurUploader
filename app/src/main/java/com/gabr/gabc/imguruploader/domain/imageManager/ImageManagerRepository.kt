package com.gabr.gabc.imguruploader.domain.imageManager

import arrow.core.Either

interface ImageManagerRepository {
    suspend fun getUserName(): Either<ImageManagerFailure, String>
    suspend fun uploadImage(image: ImgurImage): Either<ImageManagerFailure, Unit>
    suspend fun deleteImage(userName: String, deleteHash: String): Either<ImageManagerFailure, Unit>
    suspend fun getImages(): Either<ImageManagerFailure, List<ImgurImage>>
}