package com.gabr.gabc.imguruploader.infraestructure.imageManager

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ImageManagerCalls {
    @Headers("Authorization: Bearer a56c4119a16964694b0f4aa57dce87cd5b1269c9")
    @GET("/3/account/me/settings")
    suspend fun getUserName(): Response<JSONObject>

    @Headers("Authorization: Client-ID a622a65842de657")
    @POST("/3/upload")
    suspend fun uploadImage(@Body image: ImgurUpdateImageDto): Response<ImgurImageDto>

    @Headers("Authorization: Bearer a56c4119a16964694b0f4aa57dce87cd5b1269c9")
    @DELETE("/3/account/{userName}/image/{deleteHash}")
    suspend fun deleteImage(
        @Path("userName") userName: String,
        @Path("deleteHash") deleteHash: String
    ): Response<JSONObject>

    @Headers("Authorization: Bearer a56c4119a16964694b0f4aa57dce87cd5b1269c9")
    @GET("/3/account/me/images")
    suspend fun getImages(): Response<List<ImgurImageDto>>
}