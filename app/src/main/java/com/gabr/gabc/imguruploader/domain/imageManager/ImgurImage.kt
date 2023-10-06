package com.gabr.gabc.imguruploader.domain.imageManager

import android.net.Uri
import android.util.Base64
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurImageDto
import java.io.File

data class ImgurImage(
    val title: String,
    val description: String,
    val type: String,
    val deleteHash: String,
    val link: Uri,
)

fun ImgurImage.toDto(file: File): ImgurImageDto {
    return ImgurImageDto(
        title = title,
        description = description,
        image = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    )
}