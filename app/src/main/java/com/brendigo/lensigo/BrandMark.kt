package com.brendigo.lensigo

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

/** Accurate geometry from the user-supplied izvoz/brend/lensigo-logo.svg.
 * The same aperture is used across splash, menu, privacy and about.
 * PNG launcher source continues to use its separately verified build generator.
 */
@Composable
internal fun LensigoBrandMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val factor = size.minDimension / 100f
        if (factor <= 0f) return@Canvas
        val left = (size.width - factor * 100f) / 2f
        val top = (size.height - factor * 100f) / 2f
        translate(left, top) {
            scale(factor, factor, pivot = Offset.Zero) {
                val gradient = Brush.linearGradient(
                    colors = listOf(Color(0xFF08E7FD), Color(0xFF2679FF)),
                    start = Offset(8f, 8f), end = Offset(90f, 94f)
                )
                // SVG: circle cx50 cy50 r42 stroke-width5.
                drawCircle(brush = gradient, radius = 44.5f, center = Offset(50f, 50f))
                drawCircle(color = Color(0xFF061829), radius = 39.5f,
                    center = Offset(50f, 50f))
                val blade = Path().apply {
                    moveTo(50f, 50f)
                    lineTo(33f, 19f)
                    quadraticBezierTo(52f, 9f, 68f, 19f)
                    lineTo(66f, 48f)
                    close()
                }
                repeat(6) { index ->
                    rotate(index * 60f, pivot = Offset(50f, 50f)) {
                        drawPath(blade, brush = gradient)
                        drawPath(blade, color = Color(0xFF071626),
                            style = Stroke(width = 2f))
                    }
                }
                drawCircle(color = Color(0xFF0B1018), radius = 15f,
                    center = Offset(50f, 50f))
                drawCircle(color = Color(0xFF00D4FF), radius = 15f,
                    center = Offset(50f, 50f), style = Stroke(width = 3f))
                drawCircle(color = Color(0xFF00D4FF), radius = 6f,
                    center = Offset(50f, 50f))
            }
        }
    }
}
