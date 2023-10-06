package com.gabr.gabc.imguruploader.domain.imageManager

import arrow.core.Either

interface ImageManagerRepository {
    suspend fun getUserName(): Either<ImageManagerFailure, String>
    suspend fun uploadImage(title: String, description: String, link: String): Either<ImageManagerFailure, ImgurImage>
    suspend fun deleteImage(userName: String, deleteHash: String): Either<ImageManagerFailure, Unit>
    suspend fun getImages(): Either<ImageManagerFailure, List<ImgurImage>>
}