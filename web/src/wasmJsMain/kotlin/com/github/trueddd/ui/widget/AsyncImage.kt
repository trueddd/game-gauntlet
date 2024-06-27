package com.github.trueddd.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.github.trueddd.core.AppClient
import com.github.trueddd.data.Participant
import com.github.trueddd.di.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

// TODO: replace with coil3
@Composable
private fun AsyncImage(
    block: suspend () -> ByteArray?,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
) {
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(block) {
        image = withContext(Dispatchers.Default) {
            try {
                val byteArray = block() ?: return@withContext null
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
            contentScale = contentScale,
            modifier = modifier,
        )
    }
}

@Composable
fun AsyncImage(
    model: String,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    AsyncImage(
        block = { appClient.loadImage(model) },
        contentScale = contentScale,
        modifier = modifier
    )
}

@Composable
fun AsyncProfileBackgroundImage(
    participant: Participant,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    AsyncImage(
        block = { appClient.loadPlayerBackground(participant) },
        contentScale = contentScale,
        modifier = modifier
    )
}
