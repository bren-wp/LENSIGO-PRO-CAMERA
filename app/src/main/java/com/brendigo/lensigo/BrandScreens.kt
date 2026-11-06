package com.brendigo.lensigo

import androidx.compose.foundation.background
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
    Box(Modifier.fillMaxSize().background(Night)) {
        Column(
            Modifier.align(Alignment.Center).offset(y = (-70).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LensigoBrandMark(modifier = Modifier.size(134.dp))
            Spacer(Modifier.height(13.dp))
            Text("LENSIGO", fontWeight = FontWeight.Bold, fontSize = 46.sp,
                color = White, letterSpacing = 1.sp)
            Text("P R O   C A M E R A", fontWeight = FontWeight.Bold,
                fontSize = 15.sp, color = Accent)
            Spacer(Modifier.height(8.dp))
            Text("B Y   B R E N D I G O", fontSize = 10.sp,
                color = Muted, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(62.dp))
            Text("Svaki kadar. Tvoja priča.", color = White, fontSize = 17.sp)
        }
        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 42.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(Modifier.size(25.dp),
                color = Accent, strokeWidth = 2.dp)
            Spacer(Modifier.height(25.dp))
            Text("BRENDIGO.COM", fontSize = 10.sp, color = Muted,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LensigoWelcome(onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Night).padding(18.dp)) {
        Box(Modifier.fillMaxWidth().height(65.dp), contentAlignment = Alignment.Center) {
            Text("DOBRO DOŠLI", fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().weight(1f)
            .border(1.dp, Color(0xFF2A779B), RoundedCornerShape(24.dp))
            .background(Color(0xFF0C2A40), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center) {
            LensigoBrandMark(modifier = Modifier.size(154.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("LENSIGO  ·  PRO CAMERA", fontSize = 13.sp, color = Accent)
        Spacer(Modifier.height(22.dp))
        Text("Stvori nešto\nizvanredno.", fontSize = 31.sp,
            lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = White)
        Spacer(Modifier.height(20.dp))
        Text("Profesionalna kamera za svaki trenutak.", fontSize = 15.sp, color = Muted)
        Spacer(Modifier.height(32.dp))
        Text("●  ○  ○", modifier = Modifier.fillMaxWidth(),
            color = Accent, textAlign = TextAlign.Center, fontSize = 14.sp)
        Spacer(Modifier.height(30.dp))
        Button(onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984AD))) {
            Text("Započni", fontWeight = FontWeight.Bold, color = White)
        }
    }
}

@Composable
fun LensigoPermissions(
    cameraGranted: Boolean,
    storageGranted: Boolean,
    micGranted: Boolean,
    onCamera: () -> Unit,
    onMic: () -> Unit,
    onContinue: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Night).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("DOZVOLE APLIKACIJE", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = White)
        Text("Odaberi pristup koji želiš dati. Obrada i spremanje ostaju na uređaju.",
            fontSize = 14.sp, color = Muted)
        Spacer(Modifier.height(18.dp))
        PermissionRow("Kamera", "Za fotografije i video", cameraGranted, onCamera,
            icon = { Icon(Icons.Default.CameraAlt, null, tint = Accent) })
        PermissionRow("Spremanje", "Android 7–9 zahtijeva zasebnu dozvolu",
            storageGranted, onCamera,
            icon = { Icon(Icons.Default.Save, null, tint = Accent) })
        PermissionRow("Mikrofon", "Neobavezan: samo video sa zvukom",
            micGranted, onMic, icon = { Icon(Icons.Default.Mic, null, tint = Accent) })
        Spacer(Modifier.weight(1f))
        Text("LENSIGO nema oglase ni mrežnu dozvolu.", color = Muted, fontSize = 12.sp)
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984AD))) {
            Text("Nastavi", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PermissionRow(
    title: String, detail: String, granted: Boolean,
    onClick: () -> Unit, icon: @Composable () -> Unit
) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF102E45), RoundedCornerShape(12.dp))
        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 11.sp)
        }
        TextButton(onClick = onClick, enabled = !granted) {
            Text(if (granted) "ODOBRENO" else "DOPUSTI", fontSize = 11.sp,
                color = if (granted) Accent else White)
        }
    }
}
