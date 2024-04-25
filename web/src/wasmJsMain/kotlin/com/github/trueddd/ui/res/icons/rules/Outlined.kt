package com.github.trueddd.ui.res.icons.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
val Icons.Outlined.Rules: ImageVector
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
                moveTo(3.0f, 5.0f)
                verticalLineToRelative(14.0f)
                horizontalLineToRelative(18.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(3.0f)
                close()
                moveTo(7.0f, 7.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(7.0f)
                horizontalLineTo(7.0f)
                close()
                moveTo(5.0f, 13.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(5.0f)
                close()
                moveTo(5.0f, 15.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(15.0f)
                close()
                moveTo(19.0f, 17.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(10.0f)
                verticalLineTo(17.0f)
                close()
                moveTo(19.0f, 13.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(10.0f)
                verticalLineTo(13.0f)
                close()
                moveTo(19.0f, 9.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(7.0f)
                horizontalLineToRelative(10.0f)
                verticalLineTo(9.0f)
                close()
            }
            .build()
        return savedRules!!
    }

private var savedRules: ImageVector? = null
