package com.gabr.gabc.imguruploader.domain.imageManager

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.gabr.gabc.imguruploader.infraestructure.imageManager.models.ImgurImageDto
import java.io.ByteArrayOutputStream

data class ImgurImage(
    val title: String,
    val description: String,
    val deleteHash: String = "",
    val link: Uri,
)

fun ImgurImage.toDto(contentResolver: ContentResolver): ImgurImageDto {
    val imageStream = contentResolver.openInputStream(link)
    val selectedImage = BitmapFactory.decodeStream(imageStream)
    val baos = ByteArrayOutputStream()
    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val b = baos.toByteArray()
    val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)

    return ImgurImageDto(
        title = title,
        description = description,
        image = encodedImage
    )
}