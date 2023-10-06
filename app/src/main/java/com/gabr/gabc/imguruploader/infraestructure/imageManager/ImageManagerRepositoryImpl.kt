package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImgurImage
import retrofit2.HttpException
import javax.inject.Inject

class ImageManagerRepositoryImpl @Inject constructor(
    private val http: HttpRepository,
    private val res: StringResourcesProvider,
) : ImageManagerRepository {
    override suspend fun getUserName(): Either<ImageManagerFailure, String> {
        return try {
            val service = http.getImageManagerService()
            val result = service.getUserName()
            if (result.isSuccessful) {
                Right(result.body()!!.getString("account_url"))
            } else {
                Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
        }
    }

    override suspend fun uploadImage(title: String, description: String, link: String): Either<ImageManagerFailure, ImgurImage> {
        return try {
            val service = http.getImageManagerService()
            val result = service.uploadImage(ImgurUpdateImageDto(title, description, link))
            if (result.isSuccessful) {
                val dto = result.body()!!
                Right(ImgurImage(
                    title = dto.title,
                    description = dto.description,
                    link = Uri.parse(dto.link),
                    deleteHash = dto.deleteHash
                ))
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
            val result = service.deleteImage(userName, deleteHash)
            if (result.isSuccessful) {
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
            val result = service.getImages()
            if (result.isSuccessful) {
                val imgurImages = mutableListOf<ImgurImage>()
                val imageList = result.body()!!
                imageList.forEach { dto ->
                    imgurImages.add(ImgurImage(
                        title = dto.title,
                        description = dto.description,
                        deleteHash = dto.deleteHash,
                        link = Uri.parse(dto.link),
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