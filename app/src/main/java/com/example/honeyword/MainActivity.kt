package com.example.honeyword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Force a contrasting background so content can’t “disappear”
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppScreenProbe()
                }
            }
        }
    }
}

@Composable
private fun AppScreenProbe() {
    var taps by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Honeyword") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFFF3C4)) // pale honey color for contrast
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("If you can read this, UI is OK ✅", fontSize = 20.sp)
            Button(onClick = { taps++ }) { Text("Tap me ($taps)") }
        }
    }
}
