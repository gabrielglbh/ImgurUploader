package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.di.ContentResolverProvider
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImgurImage
import com.gabr.gabc.imguruploader.domain.imageManager.toDto
import retrofit2.HttpException
import retrofit2.await
import javax.inject.Inject

class ImageManagerRepositoryImpl @Inject constructor(
    private val http: HttpRepository,
    private val res: StringResourcesProvider,
    private val contentResolver: ContentResolverProvider,
) : ImageManagerRepository {
    override suspend fun getUserName(): Either<ImageManagerFailure, String> {
        return try {
            val service = http.getImageManagerService()
            val result = service.getUserName().await()
            if (result.success) {
                Right(result.data.getString("account_url"))
            } else {
                Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
        }
    }

    override suspend fun uploadImage(image: ImgurImage): Either<ImageManagerFailure, Unit> {
        return try {
            val service = http.getImageManagerService()
            val result = service.uploadImage(image.toDto(contentResolver.resolver())).await()
            if (result.success) {
                Right(Unit)
            } else {
                Left(ImageManagerFailure.ImageUploadFailed(res.getString(R.string.error_imgur_upload)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.ImageUploadFailed(res.getString(R.string.error_imgur_upload)))
        }
    }

    override suspend fun deleteImage(userName: String, deleteHash: String): Either<ImageManagerFailure, Unit> {
        return try {
            val service = http.getImageManagerService()
            val result = service.deleteImage(userName, deleteHash).await()
            if (result.success) {
                Right(Unit)
            } else {
                Left(ImageManagerFailure.ImageDeletionFailed(res.getString(R.string.error_imgur_delete)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.ImageDeletionFailed(res.getString(R.string.error_imgur_delete)))
        }
    }

    override suspend fun getImages(): Either<ImageManagerFailure, List<ImgurImage>> {
        return try {
            val service = http.getImageManagerService()
            val result = service.getImages().await()
            if (result.success) {
                val imgurImages = mutableListOf<ImgurImage>()
                val imageList = result.data
                imageList.forEach { json ->
                    imgurImages.add(ImgurImage(
                        title = json.get("title") as String? ?: "",
                        description = json.get("description") as String? ?: "",
                        deleteHash = json.getString("deletehash"),
                        link = Uri.parse(json.getString("link")),
                    ))
                }
                Right(imgurImages)
            } else {
                Left(ImageManagerFailure.ImagesRetrievalFailed(res.getString(R.string.error_imgur_get_images)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.ImagesRetrievalFailed(res.getString(R.string.error_imgur_get_images)))
        }
    }
}