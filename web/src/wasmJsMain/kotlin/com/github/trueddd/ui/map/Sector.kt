package com.github.trueddd.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.trueddd.map.Genre
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.localized

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun Sector(
    state: MapSectorState,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = {
                    Text(
                        text = if (state.index == 0) "Стартовая клетка" else "Клетка ${state.index}",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        when (state.genre) {
                            Genre.Special -> Text("Специальный сектор")
                            null -> {}
                            else -> Text("Жанр: ${state.genre.localized}")
                        }
                        if (state.players.isNotEmpty()) {
                            Text("Игроки на клетке: ${state.players.joinToString { it.displayName }}")
                        }
                        if (state.traps.isNotEmpty()) {
                            Text("Ловушки: ${state.traps.joinToString { it.name }}")
                        }
                        if (state.radioStation != null) {
                            Text("Радиостанция: ${state.radioStation.localized}")
                        }
                    }
                }
            )
        },
        state = rememberTooltipState(isPersistent = true),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = state.genre?.color?.let { Color(it) } ?: Colors.DefaultGenre,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            if (state.index != 0) {
                Text(
                    text = state.index.toString(),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (state.players.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    state.players.forEach {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.onSecondaryContainer, CircleShape)
                        ) {
                            Text(
                                text = it.displayName.first().uppercase(),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
