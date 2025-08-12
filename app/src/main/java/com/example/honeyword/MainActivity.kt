package com.example.honeyword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppScreen()
            }
        }
    }
}

@Composable
private fun AppScreen() {
    var current by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Honeyword") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current entry box
            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (current.isEmpty()) "Start typing…" else current.uppercase(),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Minimal input row so app is interactive
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val letters = listOf("A", "B", "C", "D")
                letters.forEach { ch ->
                    Button(onClick = { current += ch.lowercase() }) {
                        Text(ch)
                    }
                }
                OutlinedButton(onClick = { current = "" }) {
                    Text("Clear")
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "✅ Build target: simple, compiling screen.\nWe’ll swap in the star keypad next.",
                textAlign = TextAlign.Center
            )
        }
    }
}
