package com.gabr.gabc.imguruploader.infraestructure.imageManager

import android.net.Uri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.di.SharedPreferencesProvider
import com.gabr.gabc.imguruploader.di.StringResourcesProvider
import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.domain.imageManager.models.OAuth
import com.gabr.gabc.imguruploader.presentation.shared.Constants
import com.google.gson.JsonParser
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class ImageManagerRepositoryImpl @Inject constructor(
    private val http: HttpRepository,
    private val res: StringResourcesProvider,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
) : ImageManagerRepository {
    override suspend fun getSession(
        refreshToken: String,
        clientId: String,
        clientSecret: String,
        clientType: String
    ): Either<ImageManagerFailure, OAuth> {
        return try {
            val service = http.getImageManagerService()
            val result = service.getSession(
                refreshToken.toRequestBody(),
                clientId.toRequestBody(),
                clientSecret.toRequestBody(),
                clientType.toRequestBody()
            )

            if (result.isSuccessful) {
                val body = result.body()!!
                Right(OAuth(body.accessToken, body.refreshToken, body.accountUsername))
            } else {
                Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
        }
    }

    override suspend fun getUserData(userName: String): Either<ImageManagerFailure, Account> {
        return try {
            val service = http.getImageManagerService()
            val result = service.getUserData(userName)
            if (result.isSuccessful) {
                val body = result.body()!!
                val data = body.data
                Right(Account(data.username, Uri.parse(data.avatar)))
            } else {
                if (result.code() == 401 || result.code() == 403) {
                    Left(ImageManagerFailure.Unauthorized(""))
                } else {
                    Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
                }
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.UserRetrievalFailed(res.getString(R.string.error_imgur_user)))
        }
    }

    override suspend fun uploadImage(title: String, description: String, file: File): Either<ImageManagerFailure, Unit> {
        return try {
            val service = http.getImageManagerService()
            val filePart = MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())

            val token = sharedPreferencesProvider.getPref().getString(Constants.ACCESS_TOKEN, null) ?: ""
            val result = service.uploadImage(
                bearer = "Bearer $token",
                title = title.toRequestBody(),
                description = description.toRequestBody(),
                image = filePart
            )

            if (result.isSuccessful) {
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
            val token = sharedPreferencesProvider.getPref().getString(Constants.ACCESS_TOKEN, null) ?: ""
            val result = service.deleteImage("Bearer $token", userName, deleteHash)
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
            val token = sharedPreferencesProvider.getPref().getString(Constants.ACCESS_TOKEN, null) ?: ""
            val result = service.getImages("Bearer $token")
            if (result.isSuccessful) {
                val imgurImages = mutableListOf<ImgurImage>()
                val body = result.body()!!
                val data = body.data
                data.forEach { dto ->
                    imgurImages.add(
                        ImgurImage(
                            title = dto.title,
                            description = dto.description,
                            deleteHash = dto.deleteHash,
                            link = Uri.parse(dto.link),
                        )
                    )
                }
                Right(imgurImages)
            } else {
                Left(ImageManagerFailure.ImagesRetrievalFailed(res.getString(R.string.error_imgur_get_images)))
            }
        } catch (err: HttpException) {
            Left(ImageManagerFailure.ImagesRetrievalFailed(res.getString(R.string.error_imgur_get_images)))
        } catch (err: IllegalStateException) {
            Left(ImageManagerFailure.ImagesRetrievalFailed(res.getString(R.string.error_imgur_get_images)))
        }
    }
}