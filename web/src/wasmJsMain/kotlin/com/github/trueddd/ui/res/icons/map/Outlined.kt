package com.github.trueddd.ui.res.icons.map

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
val Icons.Outlined.Map: ImageVector
    get() {
        if (savedMap != null) {
            return savedMap!!
        }
        savedMap = ImageVector.Builder(
            name = "Map",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
            .materialPath {
                moveTo(20.5f, 3.0f)
                lineToRelative(-0.16f, 0.03f)
                lineTo(15.0f, 5.1f)
                lineTo(9.0f, 3.0f)
                lineTo(3.36f, 4.9f)
                curveToRelative(-0.21f, 0.07f, -0.36f, 0.25f, -0.36f, 0.48f)
                lineTo(3.0f, 20.5f)
                curveToRelative(0.0f, 0.28f, 0.22f, 0.5f, 0.5f, 0.5f)
                lineToRelative(0.16f, -0.03f)
                lineTo(9.0f, 18.9f)
                lineToRelative(6.0f, 2.1f)
                lineToRelative(5.64f, -1.9f)
                curveToRelative(0.21f, -0.07f, 0.36f, -0.25f, 0.36f, -0.48f)
                lineTo(21.0f, 3.5f)
                curveToRelative(0.0f, -0.28f, -0.22f, -0.5f, -0.5f, -0.5f)
                close()
                moveTo(10.0f, 5.47f)
                lineToRelative(4.0f, 1.4f)
                verticalLineToRelative(11.66f)
                lineToRelative(-4.0f, -1.4f)
                lineTo(10.0f, 5.47f)
                close()
                moveTo(5.0f, 6.46f)
                lineToRelative(3.0f, -1.01f)
                verticalLineToRelative(11.7f)
                lineToRelative(-3.0f, 1.16f)
                lineTo(5.0f, 6.46f)
                close()
                moveTo(19.0f, 17.54f)
                lineToRelative(-3.0f, 1.01f)
                lineTo(16.0f, 6.86f)
                lineToRelative(3.0f, -1.16f)
                verticalLineToRelative(11.84f)
                close()
            }
            .build()
        return savedMap!!
    }

private var savedMap: ImageVector? = null
