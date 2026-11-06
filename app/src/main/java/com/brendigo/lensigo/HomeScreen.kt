package com.brendigo.lensigo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HomeNavy = Color(0xFF0B2639)
private val HomeBackground = Color(0xFF091B2C)
private val HomeCard = Color(0xFF13334B)
private val HomeCyan = Color(0xFF00D4FF)
private val HomeWhite = Color(0xFFF7FCFF)
private val HomeMuted = Color(0xFFA1B6C8)

private data class CameraEntry(
    val mode: CameraMode,
    val icon: ImageVector,
    val label: String,
    val description: String
)

private val cameraEntries = listOf(
    CameraEntry(CameraMode.PHOTO, Icons.Default.CameraAlt, "Fotografija",
        "Oštri i uravnoteženi kadrovi"),
    CameraEntry(CameraMode.VIDEO, Icons.Default.Videocam, "Video",
        "SD i HD prema uređaju"),
    CameraEntry(CameraMode.PRO, Icons.Default.Tune, "Pro način",
        "Ručne kontrole uz podršku"),
    CameraEntry(CameraMode.NIGHT, Icons.Default.NightsStay, "Noć",
        "Posvjetljivanje slabog svjetla"),
    CameraEntry(CameraMode.PORTRAIT, Icons.Default.Portrait, "Portret",
        "Bokeh ili lokalni efekt"),
    CameraEntry(CameraMode.PANORAMA, Icons.Default.Panorama, "Panorama",
        "Sastavljanje više kadrova"),
    CameraEntry(CameraMode.DUAL, Icons.Default.Cameraswitch, "Dual",
        "Prednja i stražnja kamera"),
    CameraEntry(CameraMode.FILTERS, Icons.Default.AutoAwesome, "Filteri",
        "12 lokalnih fotografskih efekata")
)

@Composable
internal fun LensigoHomeScreen(
    onMode: (CameraMode) -> Unit,
    onGallery: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(HomeBackground)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 60.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(R.drawable.lensigo_symbol), "LENSIGO",
                modifier = Modifier.size(37.dp))
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text("LENSIGO", color = HomeWhite, fontWeight = FontWeight.Bold,
                    letterSpacing = .5.sp, fontSize = 19.sp)
                Text("P R O  C A M E R A", color = HomeCyan,
                    fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, "Postavke", tint = HomeWhite)
            }
        }
        HorizontalDivider(color = Color(0xFF174259))
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text("Tvoja kamera.\nTvoja priča.", color = HomeWhite, fontSize = 28.sp,
                lineHeight = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text("Odaberi način snimanja.", color = HomeMuted, fontSize = 13.sp)
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 145.dp),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp)
        ) {
            items(cameraEntries, key = { it.mode.name }) { item ->
                Column(Modifier.fillMaxWidth()
                    .heightIn(min = 130.dp)
                    .background(HomeCard, RoundedCornerShape(15.dp))
                    .border(1.dp, Color(0xFF245776), RoundedCornerShape(15.dp))
                    .clickable { onMode(item.mode) }
                    .padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(35.dp)
                        .background(Color(0xFF0B4B67), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center) {
                        Icon(item.icon, contentDescription = null, tint = HomeCyan,
                            modifier = Modifier.size(22.dp))
                    }
                    Text(item.label, color = HomeWhite, fontSize = 15.sp,
                        fontWeight = FontWeight.Bold)
                    Text(item.description, color = HomeMuted, fontSize = 11.sp,
                        lineHeight = 14.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().heightIn(min = 68.dp)
            .background(HomeNavy).padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onGallery, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.PhotoLibrary, null, tint = HomeCyan)
                Spacer(Modifier.width(7.dp))
                Text("Galerija", color = HomeWhite, fontSize = 12.sp)
            }
            TextButton(onClick = onAbout, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Info, null, tint = HomeCyan)
                Spacer(Modifier.width(7.dp))
                Text("O aplikaciji", color = HomeWhite, fontSize = 12.sp)
            }
        }
    }
}
