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
    // Example letters; swap with your puzzle later
    var center by remember { mutableStateOf('A') }
    var outer by remember { mutableStateOf(listOf('B','C','D','E','F','G')) }
    var current by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Honeyword") }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                OutlinedButton(onClick = { current = "" }) { Text("Clear") }
                OutlinedButton(onClick = {
                    // rotate the 6 outer letters
                    outer = if (outer.isNotEmpty()) outer.drop(1) + outer.first() else outer
                }) { Text("Shuffle") }
                Button(onClick = { /* TODO: submit word */ }) { Text("Submit") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current entry
            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (current.isEmpty()) "Start typing…" else current.uppercase(),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(20.dp))

            // ⭐ Six-pointed star keypad
            HoneyStarKeypad(
                center = center,
                outer = outer,
                onTap = { ch -> current += ch.lowercase() }
            )
        }
    }
}

/** A 7-button keypad arranged as a Magen David (6 around, 1 center). */
@Composable
private fun HoneyStarKeypad(
    center: Char,
    outer: List<Char>,
    onTap: (Char) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    buttonSize: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center button
        LetterButton(
            label = center,
            onClick = { onTap(center) },
            size = buttonSize
        )

        // Six outer buttons placed using polar coordinates
        val anglesDeg = listOf(90, 150, 210, 270, 330, 30) // star-like order
        val radius = size / 2 - buttonSize / 2

        anglesDeg.zip(outer.take(6)).forEach { (deg, ch) ->
            Polar(
                angleDegrees = deg.toFloat(),
                radius = radius
            ) {
                LetterButton(
                    label = ch,
                    onClick = { onTap(ch) },
                    size = buttonSize
                )
            }
        }
    }
}

/** Places content at a polar offset from the center of the parent Box. */
@Composable
private fun Polar(
    angleDegrees: Float,
    radius: Dp,
    content: @Composable () -> Unit
) {
    val rad = Math.toRadians(angleDegrees.toDouble())
    val x = kotlin.math.cos(rad)
    val y = kotlin.math.sin(rad)
    val density = androidx.compose.ui.platform.LocalDensity.current
    val dxPx = with(density) { (radius * x).toPx() }
    val dyPx = with(density) { (radius * y).toPx() }

    Box(
        modifier = Modifier
            .offset { IntOffset(dxPx.toInt() - 0, dyPx.toInt() - 0) },
        contentAlignment = Alignment.Center
    ) { content() }
}

/** A rounded Material3 letter button with consistent sizing. */
@Composable
private fun LetterButton(
    label: Char,
    onClick: () -> Unit,
    size: Dp
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(size),
        shape = MaterialTheme.shapes.large,
    ) {
        Text(label.uppercaseChar().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
