package com.brendigo.lensigo

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF0D2133)
private val Deep = Color(0xFF07131F)
private val Accent = Color(0xFF00CFF7)
private val Muted = Color(0xFF9CB3C4)
private val White = Color(0xFFF8FBFF)

@Composable
fun LensigoSplash() {
    BoxWithConstraints(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF0E2134), Color(0xFF0D2032), Color(0xFF08111E))
            )
        )
    ) {
        SplashWaves()
        val compact = maxHeight < 600.dp || maxWidth < 350.dp
        if (compact) {
            Column(
                Modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                SplashIdentity(compact = true)
                SplashFooter(compact = true)
            }
        } else {
            // Reference 01: aperture begins 24.5% down the full-height screen.
            SplashIdentity(
                compact = false,
                modifier = Modifier.align(Alignment.TopCenter)
                    .offset(y = maxHeight * .245f)
            )
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = maxHeight * .115f).size(22.dp),
                color = Accent, trackColor = Color(0xFF10597C),
                strokeWidth = 2.5.dp
            )
            Text(
                "BRENDIGO.COM",
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = maxHeight * .054f),
                color = Muted, fontSize = 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = .2.sp
            )
        }
    }
}

@Composable
private fun SplashWaves() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        // Four broad lens arcs, rather than the circles of a generic loader.
        listOf(
            .814f to Color(0xFF12547D),
            .847f to Color(0xFF00D4FF),
            .871f to Color(0xFF14486D),
            .899f to Color(0xFF00D4FF)
        ).forEach { (edge, color) ->
            val curve = Path().apply {
                moveTo(-w * .05f, h * (edge + .008f))
                quadraticBezierTo(w * .5f, h * (edge - .078f),
                    w * 1.05f, h * (edge + .008f))
            }
            drawPath(curve, color = color, style = Stroke(width = 2.dp.toPx()))
        }
    }
}

@Composable
private fun SplashIdentity(compact: Boolean, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LensigoBrandMark(Modifier.size(100.dp))
        Spacer(Modifier.height(if (compact) 8.dp else 10.dp))
        Text("LENSIGO", fontWeight = FontWeight.Bold,
            fontSize = if (compact) 34.sp else 38.sp,
            color = White, letterSpacing = .7.sp)
        Spacer(Modifier.height(5.dp))
        Text("PRO CAMERA", fontWeight = FontWeight.Bold,
            fontSize = 15.sp, color = Accent, letterSpacing = 3.sp)
        Spacer(Modifier.height(5.dp))
        Text("BY BRENDIGO", fontSize = 10.sp, color = Muted,
            fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(if (compact) 26.dp else 42.dp))
        Text("Svaki kadar. Tvoja priča.", color = White, fontSize = 17.sp,
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun SplashFooter(compact: Boolean, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            Modifier.size(if (compact) 22.dp else 25.dp),
            color = Accent, trackColor = Color(0xFF10597C),
            strokeWidth = 2.dp
        )
        Spacer(Modifier.height(if (compact) 14.dp else 25.dp))
        Text("BRENDIGO.COM", fontSize = 10.sp, color = Muted,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LensigoWelcome(onContinue: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Night)) {
        val compact = maxHeight < 680.dp || maxWidth < 350.dp
        Column(
            Modifier.fillMaxSize()
                .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(18.dp)
        ) {
            Box(Modifier.fillMaxWidth().height(65.dp), contentAlignment = Alignment.Center) {
                Text("DOBRO DOŠLI", fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
            }
            Box(
                Modifier.fillMaxWidth()
                    .then(if (compact) Modifier.height(200.dp) else Modifier.weight(1f))
                    .border(1.dp, Color(0xFF2A779B), RoundedCornerShape(24.dp))
                    .background(Color(0xFF0C2A40), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                LensigoBrandMark(modifier = Modifier.size(if (compact) 110.dp else 154.dp))
            }
            Spacer(Modifier.height(if (compact) 16.dp else 24.dp))
            Text("LENSIGO  ·  PRO CAMERA", fontSize = 13.sp, color = Accent)
            Spacer(Modifier.height(if (compact) 14.dp else 22.dp))
            Text("Stvori nešto\nizvanredno.", fontSize = 31.sp,
                lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(Modifier.height(if (compact) 12.dp else 20.dp))
            Text("Profesionalna kamera za svaki trenutak.", fontSize = 15.sp, color = Muted)
            Spacer(Modifier.height(if (compact) 20.dp else 32.dp))
            Text("●  ○  ○", modifier = Modifier.fillMaxWidth(),
                color = Accent, textAlign = TextAlign.Center, fontSize = 14.sp)
            Spacer(Modifier.height(if (compact) 18.dp else 30.dp))
            Button(onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984AD))) {
                Text("Započni", fontWeight = FontWeight.Bold, color = White)
            }
        }
    }
}

@Composable
fun LensigoPermissions(
    cameraGranted: Boolean,
    storageGranted: Boolean,
    storageRequired: Boolean,
    micGranted: Boolean,
    onCamera: () -> Unit,
    onStorage: () -> Unit,
    onMic: () -> Unit,
    onContinue: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Night)) {
        val compact = maxHeight < 630.dp || maxWidth < 350.dp
        Column(
            Modifier.fillMaxSize()
                .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("DOZVOLE APLIKACIJE", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = White)
            Text("Odaberi pristup koji želiš dati. Obrada i spremanje ostaju na uređaju.",
                fontSize = 14.sp, color = Muted)
            Spacer(Modifier.height(18.dp))
            PermissionRow("Kamera", "Za fotografije i video", cameraGranted, onCamera,
                icon = { Icon(Icons.Default.CameraAlt, null, tint = Accent) })
            PermissionRow("Spremanje",
                if (storageRequired) "Android 7–9 zahtijeva zasebnu dozvolu"
                else "Za vlastite snimke posebna dozvola nije potrebna",
                storageGranted, onStorage, notRequired = !storageRequired,
                icon = { Icon(Icons.Default.Save, null, tint = Accent) })
            PermissionRow("Mikrofon", "Neobavezan: samo video sa zvukom",
                micGranted, onMic, icon = { Icon(Icons.Default.Mic, null, tint = Accent) })
            Spacer(if (compact) Modifier.height(8.dp) else Modifier.weight(1f))
            Text("LENSIGO nema oglase ni mrežnu dozvolu.", color = Muted, fontSize = 12.sp)
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984AD))) {
                Text("Nastavi", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PermissionRow(
    title: String, detail: String, granted: Boolean,
    onClick: () -> Unit, notRequired: Boolean = false,
    icon: @Composable () -> Unit
) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF102E45), RoundedCornerShape(12.dp))
        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 11.sp)
        }
        TextButton(onClick = onClick, enabled = !granted && !notRequired) {
            Text(if (notRequired) "NIJE POTREBNA"
                else if (granted) "ODOBRENO" else "DOPUSTI",
                fontSize = 11.sp, color = if (notRequired || granted) Accent else White)
        }
    }
}
