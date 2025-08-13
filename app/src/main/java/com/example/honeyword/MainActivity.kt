package com.example.honeyword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StarScreen()
            }
        }
    }
}

@Composable
private fun StarScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3C4)), // pale honey background
        contentAlignment = Alignment.Center
    ) {
        StarOfDavid(modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f))
        // Small caption so you know it rendered
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Star render OK ✅", color = Color.Black)
        }
    }
}

/**
 * Draws a simple Magen David by overlaying two equilateral triangles:
 *  - Upright: angles (-90°, 30°, 150°)
 *  - Inverted: (90°, -30°, -150°)
 */
@Composable
private fun StarOfDavid(
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF3A2E13), // dark honey/brown
    lineWidthDp: Float = 6f
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val stroke = Stroke(width = lineWidthDp.dp.toPx())
        val sizeMin = min(size.width, size.height)
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Slight padding from edges so strokes don’t clip
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

        // Upright triangle
        val up = trianglePath(-90.0, 30.0, 150.0)
        // Inverted triangle
        val down = trianglePath(90.0, -30.0, -150.0)

        drawPath(up, color = lineColor, style = stroke)
        drawPath(down, color = lineColor, style = stroke)
    }
}
