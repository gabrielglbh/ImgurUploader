package com.gabr.gabc.imguruploader.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun ComposeImage(uri: Uri, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = uri,
        modifier = modifier,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        loading = {
            CircularProgressIndicator()
        },
        error = {
            Icon(
                Icons.Outlined.ImageNotSupported,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        },
    )
}