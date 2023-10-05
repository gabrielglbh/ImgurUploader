package com.gabr.gabc.imguruploader.infraestructure.imageManager.models

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class CustomResponse(
    @SerializedName("data")
    val data: JSONObject,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("status")
    val status: Int,
)
