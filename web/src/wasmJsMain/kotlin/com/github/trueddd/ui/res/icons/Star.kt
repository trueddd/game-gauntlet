package com.github.trueddd.ui.res.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlin.Suppress

@Suppress("UnusedReceiverParameter")
val Icons.Rounded.Star: ImageVector
    get() {
        if (savedAssistant != null) {
            return savedAssistant!!
        }
        savedAssistant = ImageVector.Builder(
            name = "Assistant",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
            .materialPath {
                moveTo(13.88f, 12.88f)
                lineTo(12.0f, 17.0f)
                lineToRelative(-1.88f, -4.12f)
                lineTo(6.0f, 11.0f)
                lineToRelative(4.12f, -1.88f)
                lineTo(12.0f, 5.0f)
                lineToRelative(1.88f, 4.12f)
                lineTo(18.0f, 11.0f)
                lineToRelative(-4.12f, 1.88f)
                close()
            }
            .build()
        return savedAssistant!!
    }

private var savedAssistant: ImageVector? = null
