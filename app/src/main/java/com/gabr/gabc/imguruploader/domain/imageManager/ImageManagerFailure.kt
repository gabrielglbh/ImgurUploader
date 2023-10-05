package com.gabr.gabc.imguruploader.domain.imageManager

sealed class ImageManagerFailure(open val error: String) {
    data class UserRetrievalFailed(override val error: String) : ImageManagerFailure(error)
    data class ImageUploadFailed(override val error: String) : ImageManagerFailure(error)
    data class ImagesRetrievalFailed(override val error: String) : ImageManagerFailure(error)
    data class ImageDeletionFailed(override val error: String) : ImageManagerFailure(error)
}
