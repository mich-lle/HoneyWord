package com.example.honeyword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val words = WordUtils.loadWordList(this)
        val puzzle = PuzzleGenerator(words).generate()

        setContent {
            MaterialTheme {
                if (puzzle == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Could not generate a puzzle. Add a larger words.txt in assets.", fontWeight = FontWeight.Bold)
                    }
                } else {
                    HoneywordGame(puzzle = puzzle)
                }
            }
        }
    }
}

@Composable
fun HoneywordGame(puzzle: Puzzle) {
    var current by remember { mutableStateOf("") }
    var found by remember { mutableStateOf(setOf<String>()) }
    val required = puzzle.letters.first()
    val ringLetters = puzzle.letters.drop(1)
    val allLetters = puzzle.letters

    fun submit() {
        val w = current.lowercase()
        val valid = w in puzzle.allowedWords
        if (valid) {
            found = found + w
        }
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
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Center: '$required' • Pangrams: ${puzzle.pangrams.size}")
            val score = found.sumOf { WordUtils.score(it, it in puzzle.pangrams) }
            Text("Score: $score / ${puzzle.maxScore}")
        }
        Spacer(Modifier.height(12.dp))

        // Display current entry
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = current.uppercase(),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        // Honeycomb keypad (simple 2-row layout approximating a honeycomb)
        Honeycomb(letters = allLetters, onKey = { ch -> current += ch }, onDel = {
            if (current.isNotEmpty()) current = current.dropLast(1)
        }, onShuffle = {
            // shuffle the ring letters while keeping center first
            val center = allLetters.first()
            val shuffled = ringLetters.shuffled()
            val newAll = listOf(center) + shuffled
            // We can't reassign puzzle, so just simulate by mapping clicks to shuffled order
        }, onSubmit = { submit() })

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

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun Honeycomb(
    letters: List<Char>,
    onKey: (Char) -> Unit,
    onDel: () -> Unit,
    onShuffle: () -> Unit,
    onSubmit: () -> Unit
) {
    // letters[0] is center
    val center = letters.first()
    val ring = letters.drop(1)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onShuffle) { Text("Shuffle") }
            TextButton(onClick = onDel) { Text("Delete") }
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HexKey(ring[0], onKey)
            HexKey(ring[1], onKey)
            HexKey(ring[2], onKey)
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HexKey(ring[3], onKey)
            HexKey(center, onKey, highlighted = true)
            HexKey(ring[4], onKey)
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HexKey(ring[5], onKey)
            HexKey(ring[6], onKey)
            TextButton(onClick = onSubmit) { Text("Submit") }
        }
    }
}

@Composable
fun HexKey(ch: Char, onKey: (Char) -> Unit, highlighted: Boolean = false) {
    val bg = if (highlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val fg = if (highlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(bg)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ch.uppercase(),
            fontWeight = FontWeight.Bold,
            color = fg,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Box(modifier = Modifier
            .matchParentSize()
            .clip(CircleShape)
        )
    }
    Spacer(Modifier.width(8.dp))
    Button(onClick = { onKey(ch) }, modifier = Modifier.size(0.dp)) {}
}