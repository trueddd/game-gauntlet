package com.github.trueddd.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.trueddd.core.AppClient
import com.github.trueddd.di.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

@Composable
fun AsyncImage(
    model: String,
    modifier: Modifier = Modifier,
) {
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    val appClient = remember { get<AppClient>() }
    LaunchedEffect(model) {
        image = withContext(Dispatchers.Default) {
            try {
                val byteArray = appClient.loadImage(model)
                    ?: return@withContext null
                Image.makeFromEncoded(byteArray)
                    .toComposeImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
    if (image == null) {
        Box(modifier)
    } else {
        Image(
            bitmap = image!!,
            contentDescription = null,
            modifier = modifier,
        )
    }
}
