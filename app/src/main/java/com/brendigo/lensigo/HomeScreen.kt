package com.brendigo.lensigo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The 04_home reference has: brand header, large visual story banner,
 * two-column camera modes and one anchored gallery action.
 * All modes are real navigation actions, never dummy/demo surfaces.
 */
private val Midnight = Color(0xFF0E2336)
private val CardColor = Color(0xFF1B394F)
private val Cyan = Color(0xFF00D4FF)
private val White = Color(0xFFF7FCFF)
private val Muted = Color(0xFF9FB8CA)
private val Outline = Color(0xFF2D6884)

private data class ModeTile(
    val mode: CameraMode,
    val label: String,
    val hint: String,
    val icon: ImageVector
)

private val tiles = listOf(
    ModeTile(CameraMode.PHOTO, "Foto", "Fotografiranje", Icons.Default.CameraAlt),
    ModeTile(CameraMode.VIDEO, "Video", "Videosnimanje", Icons.Default.Videocam),
    ModeTile(CameraMode.PRO, "Pro način", "Ručne kontrole", Icons.Default.Tune),
    ModeTile(CameraMode.NIGHT, "Noćni način", "Noćno snimanje", Icons.Default.NightsStay),
    ModeTile(CameraMode.PORTRAIT, "Portret", "Portretni način", Icons.Default.PersonOutline),
    ModeTile(CameraMode.PANORAMA, "Panorama", "Panoramsko snimanje", Icons.Default.Panorama),
    ModeTile(CameraMode.DUAL, "Dual kamera", "Dvije kamere", Icons.Default.ViewWeek),
    ModeTile(CameraMode.FILTERS, "Filteri", "Fotografski filteri", Icons.Default.AutoAwesome)
)

@Composable
private fun BrandBanner() {
    val fontScale = LocalDensity.current.fontScale
    val bannerHeight = when {
        fontScale >= 1.6f -> 250.dp
        fontScale >= 1.2f -> 200.dp
        else -> 164.dp
    }
    Box(Modifier.fillMaxWidth().height(bannerHeight)
        .clip(RoundedCornerShape(21.dp))
        .border(1.dp, Outline, RoundedCornerShape(21.dp))) {
        // Scenic local rendering until the source JPEG is bundled. No network request.
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color(0xFFB96D3C), .37f to Color(0xFFF29A33),
                    .56f to Color(0xFF783A30), 1f to Color(0xFF09283B)
                )
            )
            drawCircle(Color(0xFFFFCE6D), radius = h * .12f,
                center = Offset(w * .67f, h * .31f))
            val mountains = Path().apply {
                moveTo(w * .4f, h * .73f)
                lineTo(w * .52f, h * .56f)
                lineTo(w * .63f, h * .64f)
                lineTo(w * .79f, h * .39f)
                lineTo(w, h * .57f)
                lineTo(w, h)
                lineTo(w * .4f, h)
                close()
            }
            drawPath(mountains, Color(0xFF263044))
            val shore = Path().apply {
                moveTo(0f, h * .78f)
                lineTo(w * .19f, h * .72f)
                lineTo(w * .35f, h * .83f)
                lineTo(w * .59f, h * .70f)
                lineTo(w * .68f, h)
                lineTo(0f, h)
                close()
            }
            drawPath(shore, Color(0xFF123748))
            drawRect(Color(0x6605111D))
        }
        Column(Modifier.align(Alignment.BottomStart).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("OTKRIJ NOVU PERSPEKTIVU", color = Cyan,
                fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Svaki kadar.\nTvoja priča.", color = White,
                fontSize = 25.sp, lineHeight = 27.sp,
                fontWeight = FontWeight.Bold)
            Text("Profesionalna kamera u tvom džepu.",
                color = White.copy(alpha = .87f), fontSize = 12.sp)
        }
    }
}

@Composable
internal fun LensigoHomeScreen(
    onMode: (CameraMode) -> Unit,
    onGallery: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(Midnight)) {
        val tablet = maxWidth >= 600.dp
        val narrowPhone = maxWidth < 340.dp
        val fontScale = LocalDensity.current.fontScale
        val maxContentWidth = if (tablet) 950.dp else 540.dp
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier.fillMaxWidth().widthIn(max = maxContentWidth)
                    .heightIn(min = 77.dp).padding(horizontal = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LensigoBrandMark(Modifier.size(48.dp).clickable(
                    onClickLabel = "O aplikaciji LENSIGO",
                    role = Role.Button, onClick = onAbout
                ))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("LENSIGO", color = White,
                        fontSize = if (fontScale >= 1.6f) 22.sp else 27.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = .7.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("PRO CAMERA · BY BRENDIGO", color = Cyan,
                        fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, "Postavke",
                        modifier = Modifier.size(25.dp), tint = White)
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (tablet) 4 else if (narrowPhone) 1 else 2),
                modifier = Modifier.weight(1f).widthIn(max = maxContentWidth),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 20.dp,
                    bottom = 12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BrandBanner()
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Načini snimanja", color = White,
                        fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        modifier = Modifier.padding(top = 13.dp, bottom = 4.dp))
                }
                items(tiles, key = { it.mode.name }) { tile ->
                    Column(
                        Modifier.fillMaxWidth().heightIn(min = 105.dp)
                            .background(CardColor, RoundedCornerShape(17.dp))
                            .border(1.dp, Outline, RoundedCornerShape(17.dp))
                            .clickable(onClickLabel = tile.hint, role = Role.Button) {
                                onMode(tile.mode)
                            }.padding(15.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(tile.icon, contentDescription = null, tint = Cyan,
                            modifier = Modifier.size(28.dp))
                        Spacer(Modifier.height(15.dp))
                        Text(tile.label, color = White, fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, maxLines = 2,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Box(Modifier.fillMaxWidth().widthIn(max = maxContentWidth)
                .padding(horizontal = 17.dp, vertical = 15.dp)
                .heightIn(min = 57.dp)
                .clip(RoundedCornerShape(15.dp))
                .border(1.dp, Cyan.copy(alpha = .8f), RoundedCornerShape(15.dp))
                .background(Color(0xFF0B4868))
                .clickable(onClick = onGallery), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = White,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(9.dp))
                    Text("Otvori galeriju", color = White,
                        fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
