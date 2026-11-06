package com.brendigo.lensigo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Shared design tokens measured against the Brendigo settings references.
private val Midnight = Color(0xFF0E2336)
private val Panel = Color(0xFF182F43)
private val Cyan = Color(0xFF00D4FF)
private val White = Color(0xFFF7FCFF)
private val Muted = Color(0xFF9CB4C6)
private val Border = Color(0xFF2D536A)

@Composable
private fun SettingsHeader(title: String, onBack: () -> Unit, onMenu: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 58.dp)
        .padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Natrag", tint = White) }
        Text(title, color = White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        IconButton(onClick = onMenu) { Icon(Icons.Default.Settings, "Postavke", tint = White) }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title.uppercase(), color = Cyan, fontSize = 12.sp,
        fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 13.dp, bottom = 9.dp))
    Column(Modifier.fillMaxWidth()
        .background(Panel, RoundedCornerShape(14.dp))
        .padding(horizontal = 9.dp), content = content)
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(thickness = 1.dp, color = Border,
        modifier = Modifier.padding(horizontal = 4.dp))
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    value: String? = null,
    checked: Boolean? = null,
    onClick: (() -> Unit)? = null,
    onToggle: ((Boolean) -> Unit)? = null
) {
    val active = onClick != null || onToggle != null
    Row(
        Modifier.fillMaxWidth().heightIn(min = 71.dp)
            .then(if (active) Modifier.clickable {
                if (checked != null && onToggle != null) onToggle(!checked)
                else onClick?.invoke()
            } else Modifier)
            .padding(horizontal = 7.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null,
            modifier = Modifier.size(22.dp), tint = if (checked == true) Cyan else Muted)
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(label, color = White, fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
            if (subtitle.isNotBlank()) Text(subtitle,
                color = Muted, fontSize = 11.sp, lineHeight = 15.sp)
        }
        Spacer(Modifier.width(8.dp))
        if (checked != null && onToggle != null) {
            Switch(checked = checked, onCheckedChange = onToggle,
                modifier = Modifier.heightIn(min = 40.dp),
                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0A99C1)))
        } else if (value != null) {
            Text(value, color = Muted, fontSize = 11.sp, textAlign = TextAlign.End)
        } else if (onClick != null) {
            Icon(Icons.Default.ChevronRight, null, tint = Muted,
                modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
internal fun LensigoSettingsScreen(
    grid: Boolean, timerSeconds: Int, shutterSound: Boolean,
    lowMemory: Boolean, noise: Boolean, sharpen: Boolean,
    preferSD: Boolean, jpegQuality: Int,
    hasMicPermission: Boolean,
    onGrid: (Boolean) -> Unit,
    onTimer: (Int) -> Unit,
    onSound: (Boolean) -> Unit,
    onLowMemory: (Boolean) -> Unit,
    onNoise: (Boolean) -> Unit,
    onSharpen: (Boolean) -> Unit,
    onSD: (Boolean) -> Unit,
    onCameraSettings: () -> Unit,
    onMicrophone: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    onBack: () -> Unit
) {
    var timerDialog by remember { mutableStateOf(false) }
    if (timerDialog) AlertDialog(
        onDismissRequest = { timerDialog = false },
        title = { Text("Samookidač") },
        text = {
            Column {
                listOf(0, 3, 5, 10).forEach { value ->
                    TextButton(onClick = { onTimer(value); timerDialog = false },
                        modifier = Modifier.fillMaxWidth()) {
                        Text(if (value == 0) "Isključeno" else "$value sekundi")
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { timerDialog = false }) { Text("Zatvori") } }
    )
    Column(Modifier.fillMaxSize().background(Midnight)) {
        SettingsHeader("Postavke", onBack, onCameraSettings)
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(horizontal = 17.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 600.dp)) {
                Spacer(Modifier.height(22.dp))
                Text("Postavke", color = White, fontSize = 29.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text("Prilagodi LENSIGO svojem načinu snimanja.",
                    color = Muted, fontSize = 13.sp)
                SettingsSection("Kamera") {
                    SettingsItem(Icons.Default.CameraAlt, "Kvaliteta fotografije",
                        "JPEG fotografije", "$jpegQuality %", onClick = onCameraSettings)
                    SettingsDivider()
                    SettingsItem(Icons.Default.Videocam, "Kvaliteta videozapisa",
                        "Rezolucija ovisi o kameri", if (preferSD) "SD" else "HD",
                        onClick = { onSD(!preferSD) })
                    SettingsDivider()
                    SettingsItem(Icons.Default.GridOn, "Mreža kadra",
                        "Pomoć pri kompoziciji", checked = grid, onToggle = onGrid)
                    SettingsDivider()
                    SettingsItem(Icons.Default.Timer, "Samookidač", "Odgoda okidača",
                        if (timerSeconds == 0) "Isključeno" else "$timerSeconds s",
                        onClick = { timerDialog = true })
                    SettingsDivider()
                    SettingsItem(Icons.Default.VolumeUp, "Zvuk okidača", "Zvuk pri fotografiranju",
                        checked = shutterSound, onToggle = onSound)
                }
                SettingsSection("Privatnost i pohrana") {
                    SettingsItem(Icons.Default.Folder, "Mjesto spremanja",
                        "Pictures/LENSIGO · Movies/LENSIGO", "Uređaj")
                    SettingsDivider()
                    SettingsItem(Icons.Default.CloudOff, "Lokalna obrada",
                        "Fotografije se obrađuju na uređaju", "Aktivna")
                    SettingsDivider()
                    SettingsItem(Icons.Default.VerifiedUser, "Dozvole",
                        "Kamera, mikrofon i pristup datotekama", onClick = onPrivacy)
                }
                SettingsSection("Obrada fotografija") {
                    SettingsItem(Icons.Default.Memory, "Štedljiva obrada",
                        "Manje korištenje memorije", checked = lowMemory,
                        onToggle = onLowMemory)
                    SettingsDivider()
                    SettingsItem(Icons.Default.BlurOn, "Smanjenje šuma",
                        "Blago zaglađivanje slike", checked = noise, onToggle = onNoise)
                    SettingsDivider()
                    SettingsItem(Icons.Default.AutoFixHigh, "Izoštravanje",
                        "Dodatno naglašavanje rubova", checked = sharpen,
                        onToggle = onSharpen)
                }
                SettingsSection("Aplikacija") {
                    SettingsItem(Icons.Default.Language, "Jezik sučelja",
                        "Trenutačni jezik", "Hrvatski")
                    SettingsDivider()
                    SettingsItem(Icons.Default.NightsStay, "Izgled",
                        "Tamna tema", "Tamna")
                    SettingsDivider()
                    SettingsItem(Icons.Default.Info, "O aplikaciji",
                        "LENSIGO by Brendigo", onClick = onAbout)
                    SettingsDivider()
                    SettingsItem(Icons.Default.Mic, "Mikrofon za video",
                        "Snimanje videozapisa sa zvukom",
                        if (hasMicPermission) "Dopušten" else "Dopusti",
                        onClick = onMicrophone)
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
internal fun LensigoCameraSettingsScreen(
    jpegQuality: Int,
    grid: Boolean,
    preferSD: Boolean,
    noise: Boolean,
    sharpen: Boolean,
    lowMemory: Boolean,
    hasMicrophone: Boolean,
    onQuality: (Int) -> Unit,
    onQualityFinished: () -> Unit,
    onGrid: (Boolean) -> Unit,
    onSD: (Boolean) -> Unit,
    onNoise: (Boolean) -> Unit,
    onSharpen: (Boolean) -> Unit,
    onLowMemory: (Boolean) -> Unit,
    onMicrophone: () -> Unit,
    onBack: () -> Unit,
    onAbout: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Midnight)) {
        SettingsHeader("Postavke kamere", onBack, onAbout)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 17.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 600.dp)) {
                Spacer(Modifier.height(20.dp))
                Text("Snimanje", color = White, fontSize = 27.sp,
                    fontWeight = FontWeight.Bold)
                Text("Prilagodi fotografije i videozapise.", color = Muted, fontSize = 13.sp)
                SettingsSection("Fotografija") {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, null, tint = Muted)
                            Spacer(Modifier.width(14.dp))
                            Text("Kvaliteta JPEG fotografije", color = White,
                                fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("$jpegQuality %", color = Cyan, fontSize = 12.sp)
                        }
                        Slider(value = jpegQuality.toFloat(), valueRange = 70f..100f,
                            onValueChange = { onQuality(it.toInt()) },
                            onValueChangeFinished = onQualityFinished)
                    }
                    SettingsDivider()
                    SettingsItem(Icons.Default.GridOn, "Mreža kadra",
                        "Pravilo trećina", checked = grid, onToggle = onGrid)
                    SettingsDivider()
                    SettingsItem(Icons.Default.Memory, "Štedljiva obrada",
                        "Manja potrošnja memorije", checked = lowMemory,
                        onToggle = onLowMemory)
                    SettingsDivider()
                    SettingsItem(Icons.Default.BlurOn, "Smanjenje šuma",
                        "Blago zaglađivanje slike", checked = noise, onToggle = onNoise)
                    SettingsDivider()
                    SettingsItem(Icons.Default.AutoFixHigh, "Izoštravanje",
                        "Naglašavanje rubova", checked = sharpen, onToggle = onSharpen)
                }
                SettingsSection("Video") {
                    SettingsItem(Icons.Default.Videocam, "Kvaliteta videosnimanja",
                        "SD ili HD prema uređaju", checked = !preferSD,
                        onToggle = { hd -> onSD(!hd) })
                    SettingsDivider()
                    SettingsItem(Icons.Default.Mic, "Snimanje zvuka",
                        "Dozvola za mikrofon",
                        if (hasMicrophone) "Dopušten" else "Dopusti",
                        onClick = onMicrophone)
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
