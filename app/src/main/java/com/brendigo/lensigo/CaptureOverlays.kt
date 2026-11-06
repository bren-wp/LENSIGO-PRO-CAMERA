package com.brendigo.lensigo

import android.widget.FrameLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private val Ice = Color(0xFFF8FCFF)
private val Cyan = Color(0xFF00CFF7)
private val Ink = Color(0xFF0D2133)

@Composable
fun BoxScope.PanoramaGuide(
    frameCount: Int, busy: Boolean,
    onFinish: () -> Unit, onCancel: () -> Unit
) {
    Column(Modifier.align(Alignment.Center).fillMaxWidth(.85f)
        .background(Color.Black.copy(alpha = .47f), RoundedCornerShape(13.dp))
        .border(1.dp, Ice.copy(alpha = .88f), RoundedCornerShape(13.dp))
        .padding(horizontal = 12.dp, vertical = 17.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("←  Pomaknite uređaj udesno  →", color = Ice, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { frameCount / PanoramaComposer.MAX_FRAMES.toFloat() },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = Cyan, trackColor = Ice.copy(alpha = .26f))
        Spacer(Modifier.height(9.dp))
        Text("Fotografija " + (frameCount + 1).coerceAtMost(PanoramaComposer.MAX_FRAMES) +
            "/" + PanoramaComposer.MAX_FRAMES + " · Preklapanje oko 28%",
            color = Ice, fontSize = 11.sp)
    }
    if (frameCount > 0) {
        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 66.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            OutlinedButton(onClick = onCancel, enabled = !busy) {
                Text("ODUSTANI", color = Ice, fontSize = 11.sp)
            }
            Button(onClick = onFinish,
                enabled = frameCount >= PanoramaComposer.MIN_FRAMES && !busy,
                colors = ButtonDefaults.buttonColors(containerColor = Cyan)) {
                Text("ZAVRŠI", color = Ink, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun BoxScope.DualPreview(
    step: Int, concurrent: Boolean, pipLeft: Boolean, onView: (PreviewView) -> Unit
) {
    Box(
        Modifier.align(if (pipLeft) Alignment.TopStart else Alignment.TopEnd)
            .padding(top = 62.dp, start = 17.dp, end = 17.dp)
            .fillMaxWidth(.32f).aspectRatio(.77f)
            .border(2.dp, Ice, RoundedCornerShape(13.dp))
            .background(Ink, RoundedCornerShape(13.dp))
    ) {
        if (step == 0) {
            AndroidView(
                factory = { ctx -> PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    onView(this)
                } },
                modifier = Modifier.fillMaxSize(),
                update = { onView(it) }
            )
        }
        if (!concurrent) {
            Box(Modifier.fillMaxSize()
                .background(Ink, RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center) {
                Text(if (step == 0) "Prednja kamera\nnakon 1. snimke"
                    else "Snimi prednjom\nkamerom",
                    color = Ice, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }
    Text(
        if (concurrent) "Istodobni prikaz · 2 kamere"
        else if (step == 0) "1/2 · Stražnja kamera"
        else "2/2 · Prednja kamera",
        color = Ice, fontSize = 12.sp,
        modifier = Modifier.align(Alignment.BottomCenter)
            .padding(bottom = 76.dp)
            .background(Color.Black.copy(alpha = .77f), RoundedCornerShape(8.dp))
            .padding(7.dp)
    )
}
