package com.gabr.gabc.imguruploader.infraestructure.imageManager

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageManagerCalls {
    @Headers("Authorization: Bearer TOKEN")
    @GET("/3/account/me/settings")
    suspend fun getUserName(): Response<JSONObject>

    @Multipart
    @Headers(
        "Authorization: Bearer TOKEN",
        "Client-ID: TOKEN"
    )
    @POST("/3/upload")
    suspend fun uploadImage(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<JSONObject>

    @Headers("Authorization: Bearer TOKEN")
    @DELETE("/3/account/{userName}/image/{deleteHash}")
    suspend fun deleteImage(
        @Path("userName") userName: String,
        @Path("deleteHash") deleteHash: String
    ): Response<JSONObject>

    @Headers("Authorization: Bearer TOKEN")
    @GET("/3/account/me/images")
    suspend fun getImages(): Response<List<ImgurImageDto>>
}