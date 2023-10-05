package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.gabr.gabc.imguruploader.domain.imageManager.ImgurImage
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.CustomListResponse
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.CustomResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ImageManagerCalls {
    @Headers("Authorization: Bearer accessToken")
    @GET("account/me/settings")
    suspend fun getUserName(): Call<CustomResponse>

    @Headers("Authorization: Client-ID clientId")
    @POST("upload")
    suspend fun uploadImage(@Body image: ImgurImage): Call<CustomResponse>

    @Headers("Authorization: Bearer accessToken")
    @DELETE("account/{userName}/image/{deleteHash}")
    suspend fun deleteImage(
        @Path("userName") userName: String,
        @Path("deleteHash") deleteHash: String
    ): Call<CustomResponse>

    @Headers("Authorization: Bearer accessToken")
    @GET("account/me/images")
    suspend fun getImages(): Call<CustomListResponse>
}