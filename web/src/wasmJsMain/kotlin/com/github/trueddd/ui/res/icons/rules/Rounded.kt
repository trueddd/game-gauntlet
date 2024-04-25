package com.github.trueddd.ui.res.icons.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
val Icons.Rounded.Rules: ImageVector
    get() {
        if (savedRules != null) {
            return savedRules!!
        }
        savedRules = ImageVector.Builder(
            name = "Rules",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
            .materialPath {
                moveTo(3.0f, 14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(3.0f)
                verticalLineTo(14.0f)
                close()
                moveTo(3.0f, 19.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(3.0f)
                verticalLineTo(19.0f)
                close()
                moveTo(3.0f, 9.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(3.0f)
                verticalLineTo(9.0f)
                close()
                moveTo(8.0f, 14.0f)
                horizontalLineToRelative(13.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(8.0f)
                verticalLineTo(14.0f)
                close()
                moveTo(8.0f, 19.0f)
                horizontalLineToRelative(13.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(8.0f)
                verticalLineTo(19.0f)
                close()
                moveTo(8.0f, 5.0f)
                verticalLineToRelative(4.0f)
                horizontalLineToRelative(13.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(8.0f)
                close()
            }
            .build()
        return savedRules!!
    }

private var savedRules: ImageVector? = null
