package com.github.trueddd.ui.rules

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.core.ServerRouter
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
import com.github.trueddd.ui.widget.AsyncImage
import com.github.trueddd.util.applyModifiersDecoration
import com.github.trueddd.utils.wheelItems
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
private fun WheelItem.contains(text: String): Boolean {
    return name.contains(text, ignoreCase = true) || description.contains(text, ignoreCase = true)
}

@Stable
private fun WheelItem.isAnyOfTypes(types: ImmutableList<WheelItemView>): Boolean {
    return when (this) {
        is WheelItem.Effect.Buff -> types.contains(WheelItemView.Buff)
        is WheelItem.Effect.Debuff -> types.contains(WheelItemView.Debuff)
        is WheelItem.InventoryItem -> types.contains(WheelItemView.Inventory)
        is WheelItem.PendingEvent -> types.contains(WheelItemView.PendingEvent)
        is WheelItem.Event -> types.contains(WheelItemView.Event)
    }
}

@Composable
fun AllWheelItems(
    modifier: Modifier = Modifier,
) {
    val router = remember { get<ServerRouter>() }
    var searchText by remember { mutableStateOf("") }
    var selectedTypes by remember { mutableStateOf(persistentListOf<WheelItemView>()) }
    val visibleItems by remember {
        derivedStateOf {
            wheelItems
                .filter { it.contains(searchText) }
                .filter { selectedTypes.isEmpty() || it.isAnyOfTypes(selectedTypes) }
        }
    }
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text(text = "Поиск...") },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchText.isNotEmpty(),
                    enter = slideInHorizontally { it } + fadeIn(),
                    exit = slideOutHorizontally { it } + fadeOut(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        modifier = Modifier
                            .clickable { searchText = "" }
                            .pointerHoverIcon(PointerIcon.Hand)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        ) {
            WheelItemView.All.forEach {
                WheelItemChip(
                    view = it,
                    selected = selectedTypes.contains(it),
                    onClick = {
                        selectedTypes = if (selectedTypes.contains(it)) {
                            selectedTypes.remove(it)
                        } else {
                            selectedTypes.add(it)
                        }
                    }
                )
            }
        }
        val lazyListState = rememberLazyListState()
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 320.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            modifier = Modifier
                .fillMaxWidth()
                .simpleVerticalScrollbar(
                    state = lazyListState,
                    width = 6.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            items(visibleItems) { item ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    AsyncImage(
                        model = router.wheelItemIconUrl(item.iconId),
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .drawWithContent {
                                drawContent()
                                drawRoundRect(
                                    color = Color(item.color),
                                    style = Stroke(8.dp.toPx()),
                                    cornerRadius = CornerRadius(8.dp.toPx())
                                )
                            }
                    )
                    SelectionContainer {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = item.description.applyModifiersDecoration(),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
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
    val duration = if (state.isScrollInProgress) 150 else 1000

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
            val scrollbarHeight = 36.dp.toPx()

            drawRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx() - 16.dp.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha,
            )
        }
    }
}
