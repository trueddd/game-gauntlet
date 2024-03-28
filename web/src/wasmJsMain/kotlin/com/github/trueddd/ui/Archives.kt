package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.AppClient
import com.github.trueddd.di.get

@Composable
fun Archives(
    modifier: Modifier = Modifier,
) {
    val appClient = remember { get<AppClient>() }
    var gameName by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Game")
        TextField(
            value = gameName,
            onValueChange = { gameName = it },
        )
        Button(
            onClick = { appClient.searchGame(gameName) },
            enabled = gameName.isNotBlank(),
        ) {
            Text(text = "Search")
        }
    }
}
