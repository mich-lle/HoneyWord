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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AppScreen() } }
    }
}

@Composable
private fun AppScreen() {
    var center by remember { mutableStateOf('A') }
    var outer by remember { mutableStateOf(listOf('B','C','D','E','F','G')) }
    var current by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Honeyword") }) },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                OutlinedButton(onClick = { current = "" }) { Text("Clear") }
                OutlinedButton(onClick = {
                    outer = if (outer.isNotEmpty()) outer.drop(1) + outer.first() else outer
                }) { Text("Shuffle") }
                Button(onClick = { /* TODO submit */ }) { Text("Submit") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            HoneyStarKeypad(center = center, outer = outer, onTap = { ch -> current += ch.lowercase() })
        }
    }
}

@Composable
private fun HoneyStarKeypad(
    center: Char,
    outer: List<Char>,
    onTap: (Char) -> Unit,
    size: Dp = 280.dp,
    buttonSize: Dp = 56.dp
) {
    Box(modifier = Modifier.size(size).padding(8.dp), contentAlignment = Alignment.Center) {
        LetterButton(center, { onTap(center) }, buttonSize)
        val angles = listOf(90, 150, 210, 270, 330, 30)
        val radius = size / 2 - buttonSize / 2
        angles.zip(outer.take(6)).forEach { (deg, ch) ->
            Polar(deg.toFloat(), radius) { LetterButton(ch, { onTap(ch) }, buttonSize) }
        }
    }
}
@Composable private fun Polar(angleDegrees: Float, radius: Dp, content: @Composable () -> Unit) {
    val rad = Math.toRadians(angleDegrees.toDouble())
    val dx = cos(rad); val dy = sin(rad)
    val density = androidx.compose.ui.platform.LocalDensity.current
    val x = with(density) { (radius * dx).toPx() }.toInt()
    val y = with(density) { (radius * dy).toPx() }.toInt()
    Box(modifier = Modifier.offset { IntOffset(x, y) }, contentAlignment = Alignment.Center) { content() }
}
@Composable private fun LetterButton(label: Char, onClick: () -> Unit, size: Dp) {
    Button(onClick = onClick, modifier = Modifier.size(size), shape = MaterialTheme.shapes.large) {
        Text(label.uppercaseChar().toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
