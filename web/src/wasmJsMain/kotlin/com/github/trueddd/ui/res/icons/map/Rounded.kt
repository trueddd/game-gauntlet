package com.github.trueddd.ui.res.icons.map

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
val Icons.Rounded.Map: ImageVector
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
                moveTo(14.65f, 4.98f)
                lineToRelative(-5.0f, -1.75f)
                curveToRelative(-0.42f, -0.15f, -0.88f, -0.15f, -1.3f, -0.01f)
                lineTo(4.36f, 4.56f)
                curveTo(3.55f, 4.84f, 3.0f, 5.6f, 3.0f, 6.46f)
                verticalLineToRelative(11.85f)
                curveToRelative(0.0f, 1.41f, 1.41f, 2.37f, 2.72f, 1.86f)
                lineToRelative(2.93f, -1.14f)
                curveToRelative(0.22f, -0.09f, 0.47f, -0.09f, 0.69f, -0.01f)
                lineToRelative(5.0f, 1.75f)
                curveToRelative(0.42f, 0.15f, 0.88f, 0.15f, 1.3f, 0.01f)
                lineToRelative(3.99f, -1.34f)
                curveToRelative(0.81f, -0.27f, 1.36f, -1.04f, 1.36f, -1.9f)
                verticalLineTo(5.69f)
                curveToRelative(0.0f, -1.41f, -1.41f, -2.37f, -2.72f, -1.86f)
                lineToRelative(-2.93f, 1.14f)
                curveToRelative(-0.22f, 0.08f, -0.46f, 0.09f, -0.69f, 0.01f)
                close()
                moveTo(15.0f, 18.89f)
                lineToRelative(-6.0f, -2.11f)
                verticalLineTo(5.11f)
                lineToRelative(6.0f, 2.11f)
                verticalLineToRelative(11.67f)
                close()
            }
            .build()
        return savedMap!!
    }

private var savedMap: ImageVector? = null
