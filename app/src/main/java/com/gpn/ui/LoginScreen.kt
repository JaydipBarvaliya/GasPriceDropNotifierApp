package com.gpn.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onGoogleSignIn: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome to Gas Price Notifier!", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onGoogleSignIn) {
            Text(text = "Sign in with Google")
        }
    }
}
