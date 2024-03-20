package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.core.AuthManager
import com.github.trueddd.data.Participant
import com.github.trueddd.di.get
import com.github.trueddd.theme.Colors

@Composable
fun Profile(
    participant: Participant?,
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
        if (participant != null) {
            Text(
                text = "User: ${participant.name}"
            )
            Button(
                onClick = { authManager.logout() },
            ) {
                Text(text = "Logout")
            }
        } else {
            Button(
                onClick = { authManager.requestAuth() },
            ) {
                Text(text = "Login")
            }
        }
    }
}
