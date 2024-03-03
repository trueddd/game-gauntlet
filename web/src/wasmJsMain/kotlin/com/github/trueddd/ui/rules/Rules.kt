package com.github.trueddd.ui.rules

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.core.AppClient
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.theme.Colors
import com.github.trueddd.ui.widget.AsyncImage
import com.github.trueddd.util.color

private fun WheelItem.contains(text: String): Boolean {
    return name.contains(text, ignoreCase = true) || description.contains(text, ignoreCase = true)
}

private fun WheelItem.isAnyOfTypes(types: List<WheelItemView>): Boolean {
    return when (this) {
        is WheelItem.Effect.Buff -> types.contains(WheelItemView.Buff)
        is WheelItem.Effect.Debuff -> types.contains(WheelItemView.Debuff)
        is WheelItem.InventoryItem -> types.contains(WheelItemView.Inventory)
        is WheelItem.PendingEvent -> types.contains(WheelItemView.PendingEvent)
        is WheelItem.Event -> types.contains(WheelItemView.Event)
    }
}

@Composable
fun Rules(
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    var searchText by remember { mutableStateOf("") }
    var selectedTypes by remember { mutableStateOf(emptyList<WheelItemView>()) }
    var items by remember { mutableStateOf(emptyList<WheelItem>()) }
    var visibleItems by remember { mutableStateOf(emptyList<WheelItem>()) }
    LaunchedEffect(Unit) {
        items = appClient.getItems()
        visibleItems = items
            .filter { it.contains(searchText) }
            .filter { selectedTypes.isEmpty() || it.isAnyOfTypes(selectedTypes) }
    }
    LaunchedEffect(searchText, selectedTypes) {
        visibleItems = items
            .filter { it.contains(searchText) }
            .filter { selectedTypes.isEmpty() || it.isAnyOfTypes(selectedTypes) }
    }
    Column(
        modifier = modifier
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Colors.Text,
                cursorColor = Colors.Primary,
                focusedIndicatorColor = Colors.Primary,
            ),
            placeholder = {
                Text(
                    text = "Поиск...",
                    color = Colors.TextSecondary,
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchText.isNotEmpty(),
                    enter = slideInHorizontally { it } + fadeIn(),
                    exit = slideOutHorizontally { it } + fadeOut(),
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = Colors.Text,
                        modifier = Modifier
                            .clickable { searchText = "" }
                            .pointerHoverIcon(PointerIcon.Hand)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        ) {
            WheelItemView.All.forEach {
                WheelItemBadge(
                    view = it,
                    selected = selectedTypes.contains(it),
                    onClick = {
                        selectedTypes = if (selectedTypes.contains(it)) {
                            selectedTypes - it
                        } else {
                            selectedTypes + it
                        }
                    }
                )
            }
        }
        val lazyListState = rememberLazyListState()
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp),
            modifier = Modifier
                .fillMaxWidth()
                .simpleVerticalScrollbar(
                    state = lazyListState,
                    width = 4.dp,
                    color = Colors.Primary
                )
        ) {
            items(visibleItems) { item ->
                Row {
                    AsyncImage(
                        model = appClient.router.wheelItemIconUrl(item.iconId),
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White)
                            .border(4.dp, item.color, RectangleShape)
                            .padding(8.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(16.dp)
                    )
                    // TODO: add selection for text
                    //  https://github.com/JetBrains/compose-multiplatform/issues/4036
                    //  Possible workaround:
                    //  https://github.com/JetBrains/compose-multiplatform/issues/1450#issuecomment-1700968377
                    Column {
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = item.description.replace("\n", ""),
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
