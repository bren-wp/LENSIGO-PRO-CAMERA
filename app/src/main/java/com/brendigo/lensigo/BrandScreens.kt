package com.brendigo.lensigo

import androidx.compose.foundation.background
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
    BoxWithConstraints(Modifier.fillMaxSize().background(Night)) {
        // The standard phone layout keeps the reference geometry. Very short or
        // narrow viewports must scroll rather than clipping the brand or footer.
        val compact = maxHeight < 600.dp || maxWidth < 350.dp
        if (compact) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                SplashIdentity(compact = true)
                SplashFooter(compact = true)
            }
        } else {
            SplashIdentity(
                compact = false,
                modifier = Modifier.align(Alignment.Center).offset(y = (-70).dp)
            )
            SplashFooter(
                compact = false,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 42.dp)
            )
        }
    }
}

@Composable
private fun SplashIdentity(compact: Boolean, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LensigoBrandMark(modifier = Modifier.size(if (compact) 100.dp else 134.dp))
        Spacer(Modifier.height(if (compact) 8.dp else 13.dp))
        Text("LENSIGO", fontWeight = FontWeight.Bold,
            fontSize = if (compact) 36.sp else 46.sp,
            color = White, letterSpacing = 1.sp)
        Text("P R O   C A M E R A", fontWeight = FontWeight.Bold,
            fontSize = if (compact) 12.sp else 15.sp, color = Accent)
        Spacer(Modifier.height(8.dp))
        Text("B Y   B R E N D I G O", fontSize = 10.sp,
            color = Muted, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(if (compact) 26.dp else 62.dp))
        Text("Svaki kadar. Tvoja priča.", color = White,
            fontSize = if (compact) 15.sp else 17.sp,
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun SplashFooter(compact: Boolean, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            Modifier.size(if (compact) 22.dp else 25.dp),
            color = Accent, strokeWidth = 2.dp
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
