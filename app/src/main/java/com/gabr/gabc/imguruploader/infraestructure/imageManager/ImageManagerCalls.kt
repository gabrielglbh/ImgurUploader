package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.AccountDto
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurImageDto
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurResponse
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.OAuthDto
import com.gabr.gabc.imguruploader.presentation.shared.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageManagerCalls {
    @Multipart
    @POST("/oauth2/token")
    suspend fun getSession(
        @Part("refresh_token") refreshToken: RequestBody,
        @Part("client_id") clientId: RequestBody,
        @Part("client_secret") clientSecret: RequestBody,
        @Part("grant_type") clientType: RequestBody,
    ): Response<OAuthDto>

    @Headers("Authorization: Client-ID ${Constants.CLIENT_ID}")
    @GET("/3/account/{userName}")
    suspend fun getUserData(@Path("userName") userName: String): Response<ImgurResponse<AccountDto>>

    @Multipart
    @POST("/3/upload")
    suspend fun uploadImage(
        @Header("Authorization") bearer: String,
        @Header("Client-ID") clientID: String = Constants.CLIENT_ID,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @DELETE("/3/account/{userName}/image/{deleteHash}")
    suspend fun deleteImage(
        @Header("Authorization") bearer: String,
        @Path("userName") userName: String,
        @Path("deleteHash") deleteHash: String
    ): Response<JSONObject>

    @GET("/3/account/me/images")
    suspend fun getImages(@Header("Authorization") bearer: String): Response<ImgurResponse<List<ImgurImageDto>>>
}