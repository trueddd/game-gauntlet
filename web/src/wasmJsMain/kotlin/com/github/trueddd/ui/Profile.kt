package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.AppState
import com.github.trueddd.core.AuthManager
import com.github.trueddd.di.get
import com.github.trueddd.theme.Colors

@Composable
fun Profile(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    val authManager = remember { get<AuthManager>() }
    Column(
        modifier = modifier
            .padding(16.dp)
            .background(Colors.SecondaryBackground, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Profile")
        if (appState.user != null) {
            Text(
                text = "User: ${appState.user.name}"
            )
        } else {
            Button(
                onClick = { authManager.requestAuth() },
            ) {
                Text(text = "Login")
            }
        }
    }
}
