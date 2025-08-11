package com.example.honeyword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val words = WordUtils.loadWordList(this)
        val puzzle = PuzzleGenerator(words).generate()

        setContent {
            MaterialTheme {
                if (puzzle == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Could not generate a puzzle. Add a larger words.txt in assets.",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    HoneywordGame(puzzle)
                }
            }
        }
    }
}

@Composable
fun HoneywordGame(puzzle: Puzzle) {
    var current by remember { mutableStateOf("") }
    var found by remember { mutableStateOf(setOf<String>()) }

    // keep the center fixed and store the ring in state so Shuffle persists
    val center = puzzle.letters.first()
    var ring by remember { mutableStateOf(puzzle.letters.drop(1)) }
    val lettersForKeypad = listOf(center) + ring

    fun submit() {
        val w = current.lowercase()
        if (w in puzzle.allowedWords) found = found + w
        current = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Honeyword", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Center: '$center' • Pangrams: ${puzzle.pangrams.size}")
            val score = found.sumOf { WordUtils.score(it, it in puzzle.pangrams) }
            Text("Score: $score / ${puzzle.maxScore}")
        }
        Spacer(Modifier.height(12.dp))

        // current entry display
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = current.uppercase(),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
        }
        Spacer(Modifier.height(12.dp))

        // ⭐ Magen David keypad
        MagenDavidKeypad(
            letters = lettersForKeypad,
            onKey = { ch -> current += ch },
            onDel = { if (current.isNotEmpty()) current = current.dropLast(1) },
            onShuffle = { ring = ring.shuffled() },
            onSubmit = { submit() }
        )

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { current = "" }) { Text("Clear") }
            Button(onClick = { submit() }) { Text("Enter") }
        }

        Spacer(Modifier.height(12.dp))
        Text("Found (${found.size})", fontWeight = FontWeight.SemiBold)
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(found.sorted()) { w ->
                Text(w.uppercase(), modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
fun MagenDavidKeypad(
    letters: List<Char>,              // letters[0] = required center letter
    onKey: (Char) -> Unit,
    onDel: () -> Unit,
    onShuffle: () -> Unit,
    onSubmit: () -> Unit
) {
    val center = letters.first()
    val ring = letters.drop(1)        // 6 outer letters
    // Points around a hexagon: top, top-right, bottom-right, bottom, bottom-left, top-left
    val anglesDeg = listOf(-90, -30, 30, 90, 150, -150)

    Box(
        modifier = Modifier
            .size(300.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // draw two overlapping triangles to suggest the Star of David
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val r = w * 0.36f

            fun pt(deg: Int) = Offset(
                x = w / 2 + r * cos(Math.toRadians(deg.toDouble())).toFloat(),
                y = h / 2 + r * sin(Math.toRadians(deg.toDouble())).toFloat()
            )

            val up = listOf(pt(-90), pt(150), pt(30))
            val down = listOf(pt(90), pt(-150), pt(-30))

            drawPath(Path().apply {
                moveTo(up[0].x, up[0].y); lineTo(up[1].x, up[1].y); lineTo(up[2].x, up[2].y); close()
            }, color = MaterialTheme.colorScheme.secondaryContainer, alpha = 0.35f)

            drawPath(Path().apply {
                moveTo(down[0].x, down[0].y); lineTo(down[1].x, down[1].y); lineTo(down[2].x, down[2].y); close()
            }, color = MaterialTheme.colorScheme.secondaryContainer, alpha = 0.35f)
        }

        // outer 6 keys
        anglesDeg.forEachIndexed { i, deg ->
            val radius = 96.dp
            val rad = Math.toRadians(deg.toDouble())
            val x = radius * cos(rad)
            val y = radius * sin(rad)

            RoundKey(
                ch = ring[i],
                modifier = Modifier.offset(x.dp, y.dp),
                onClick = { onKey(ring[i]) }
            )
        }

        // center (required) key
        RoundKey(
            ch = center,
            highlighted = true,
            onClick = { onKey(center) }
        )
    }

    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onShuffle) { Text("Shuffle") }
        TextButton(onClick = onDel)     { Text("Delete") }
        Button(onClick = onSubmit)      { Text("Submit") }
    }
}

@Composable
private fun RoundKey(
    ch: Char,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    val fg = if (highlighted) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        tonalElevation = if (highlighted) 6.dp else 2.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(ch.uppercase(), fontWeight = FontWeight.Bold, color = fg)
        }
    }
}
