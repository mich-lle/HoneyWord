package com.example.honeyword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// === Palette (Option 1: flag-accurate blue + white + royal purple) ===
private val Blue = Color(0xFF0038B8)
private val White = Color(0xFFFFFFFF)
private val Purple = Color(0xFF7A2B8D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { StarWithSlotsScreen() } }
    }
}

@Composable
private fun StarWithSlotsScreen() {
    var tapped by remember { mutableStateOf("Welcome to Starrable — tap letters") }
    var showRules by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Simple top bar with title + Rules button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Starrable", color = Blue, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { showRules = true }) {
                Text("Rules")
            }
        }

        // Playfield area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Square playfield
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
            ) {
                // The star lines behind the slots
                StarOfDavid(
                    modifier = Modifier.fillMaxSize(),
                    lineColor = Blue,
                    lineWidthDp = 6f
                )

                // The 7 tappable letter slots (center + 6 around)
                LetterSlotsOverlay(
                    size = 60.dp,           // slot diameter
                    ringRadiusRatio = 0.36f, // distance of outer 6 from center (fraction of box size)
                    labels = listOf("A", "B", "C", "D", "E", "F"), // ring letters
                    centerLabel = "G",      // center letter (required)
                    onTap = { tapped = "Tapped: $it" }
                )
            }
        }

        // Debug/status text at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(tapped, color = Blue)
        }
    }

    if (showRules) {
        RulesDialog(onDismiss = { showRules = false })
    }
}

/**
 * In-app rules using the new terminology.
 * Keeps it lightweight; no navigation changes required.
 */
@Composable
private fun RulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        },
        title = { Text("How to Play") },
        text = {
            Text(
                "• Form words of 4+ letters\n" +
                "• Use only the 7 letters shown\n" +
                "• Every word must include the center letter\n" +
                "• Letters can repeat\n\n" +
                "Starweave: uses all 7 letters at least once ✨"
            )
        }
    )
}

/**
 * Places 6 circular slots around the center (at angles matching star points)
 * plus one center slot. Uses dp offsets so everything scales with the box.
 */
@Composable
private fun LetterSlotsOverlay(
    size: Dp,
    ringRadiusRatio: Float,
    labels: List<String>, // must be 6
    centerLabel: String,
    onTap: (String) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val box = min(maxWidth.value, maxHeight.value) // in dp
        val center = box / 2f
        val r = box * ringRadiusRatio

        // Angles chosen to line up with the star’s points/intersections
        val angles = listOf(-90.0, -30.0, 30.0, 90.0, 150.0, -150.0)

        // Helper to convert polar → dp offsets with slot centering
        fun polarToOffset(angleDeg: Double, radius: Float): Pair<Dp, Dp> {
            val rad = angleDeg * PI / 180.0
            val x = center + (radius * cos(rad)).toFloat() - (size.value / 2f)
            val y = center + (radius * sin(rad)).toFloat() - (size.value / 2f)
            return x.dp to y.dp
        }

        // 6 around
        angles.zip(labels).forEach { (a, label) ->
            val (ox, oy) = polarToOffset(a, r)
            LetterSlot(
                label = label,
                modifier = Modifier
                    .offset(x = ox, y = oy)
                    .size(size),
                onTap = onTap
            )
        }

        // Center
        LetterSlot(
            label = centerLabel,
            modifier = Modifier
                .offset(
                    x = (center.dp - size / 2),
                    y = (center.dp - size / 2)
                )
                .size(size),
            isRequired = true,
            onTap = onTap
        )
    }
}

@Composable
private fun LetterSlot(
    label: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    onTap: (String) -> Unit
) {
    // Required letter = purple ring, others = blue ring; white fill like the flag
    val ring = if (isRequired) Purple else Blue

    Box(
        modifier = modifier
            .border(width = 3.dp, color = ring, shape = CircleShape)
            .background(color = White, shape = CircleShape)
            .clickable { onTap(label) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = ring)
    }
}

/**
 * Draws the Magen David by overlaying two equilateral triangles.
 */
@Composable
private fun StarOfDavid(
    modifier: Modifier = Modifier,
    lineColor: Color = Blue,
    lineWidthDp: Float = 6f
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val stroke = Stroke(width = lineWidthDp.dp.toPx())
        val sizeMin = min(size.width, size.height)
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Leave edge padding to avoid stroke clipping
        val r = sizeMin * 0.42f

        fun pt(angleDeg: Double): Offset {
            val rad = angleDeg * PI / 180.0
            return Offset(
                (cx + r * cos(rad)).toFloat(),
                (cy + r * sin(rad)).toFloat()
            )
        }

        fun trianglePath(a1: Double, a2: Double, a3: Double): Path {
            val p1 = pt(a1); val p2 = pt(a2); val p3 = pt(a3)
            return Path().apply {
                moveTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                close()
            }
        }

        val up = trianglePath(-90.0, 30.0, 150.0)
        val down = trianglePath(90.0, -30.0, -150.0)

        drawPath(up, color = lineColor, style = stroke)
        drawPath(down, color = lineColor, style = stroke)
    }
}
