package com.github.trueddd.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.core.AppClient
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.theme.Colors

@Composable
fun Rules(
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    var items by remember { mutableStateOf(emptyList<WheelItem>()) }
    LaunchedEffect(Unit) {
        items = appClient.getItems()
    }
    Column(
        modifier = modifier
    ) {
        val lazyListState = rememberLazyListState()
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .simpleVerticalScrollbar(
                    state = lazyListState,
                    width = 4.dp,
                    color = Colors.Primary
                )
        ) {
            items(items) { item ->
                Row() {
//                    Image(
//                        bitmap = ImageBitmap(),
//                        contentDescription = "Icon of ${item.name}",
//                        modifier = Modifier
//                            .size(48.dp)
//                    )
                    // TODO: add selection for text
                    //  https://github.com/JetBrains/compose-multiplatform/issues/4036
                    //  Possible workaround:
                    //  https://github.com/JetBrains/compose-multiplatform/issues/1450#issuecomment-1700968377
                    Column() {
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 8.dp,
    color: Color = Color.White,
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 800

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}
