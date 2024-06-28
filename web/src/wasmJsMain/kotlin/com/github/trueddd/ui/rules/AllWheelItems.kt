package com.github.trueddd.ui.rules

import androidx.compose.animation.*
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.trueddd.core.ServerRouter
import com.github.trueddd.di.get
import com.github.trueddd.items.WheelItem
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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = 320.dp, vertical = 48.dp),
                verticalArrangement = Arrangement.spacedBy(64.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(visibleItems) { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(PlatformContext.INSTANCE)
                                .data(router.wheelItemIconUrl(item.iconId))
                                .crossfade(true)
                                .build(),
                            contentDescription = item.name,
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
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyListState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
            )
        }
    }
}
