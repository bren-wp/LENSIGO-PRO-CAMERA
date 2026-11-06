package com.brendigo.lensigo

import android.app.Activity
import android.os.Build
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val GalleryInk = Color(0xFF03080F)
private val GalleryCyan = Color(0xFF00D4FF)
private val GalleryText = Color(0xFFF7FCFF)
private val GalleryMuted = Color(0xFF9DB5C4)

@Composable
fun GalleryScreen(onBack: () -> Unit, onOpen: (LensMedia) -> Unit,
                  onSettings: () -> Unit = {}) {
    val context = LocalContext.current
    var media by remember { mutableStateOf<List<LensMedia>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sve", "Fotografije", "Videozapisi", "Favoriti")
    LaunchedEffect(Unit) {
        media = withContext(Dispatchers.IO) { MediaLibrary.list(context) }
        loading = false
    }
    val selected = media.filter { item ->
        when(tab) {
            1 -> !item.isVideo
            2 -> item.isVideo
            3 -> MediaActions.isFavorite(context, item.uri)
            else -> true
        }
    }
    Column(Modifier.fillMaxSize().background(Color(0xFF0D2133))) {
        Row(Modifier.fillMaxWidth().height(65.dp).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Natrag", tint = GalleryText)
            }
            Text("Galerija", modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                color = GalleryText, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, "Postavke", tint = GalleryText)
            }
        }
        Column(Modifier.padding(horizontal = 18.dp, vertical = 17.dp)) {
            Text("Moji trenutci", color = GalleryText, fontSize = 29.sp,
                fontWeight = FontWeight.Bold)
            Text("Fotografije i videozapisi na uređaju.",
                color = GalleryMuted, fontSize = 14.sp)
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                tabs.forEachIndexed { index, name ->
                    Box(Modifier.weight(1f).height(38.dp)
                        .background(if (tab == index) Color(0xFF0E4A68)
                            else Color(0xFF132A3C), RoundedCornerShape(22.dp))
                        .border(1.dp, Color(0xFF29546C), RoundedCornerShape(22.dp))
                        .clickable { tab = index },
                        contentAlignment = Alignment.Center) {
                        Text(name, color = if (tab == index) GalleryCyan else GalleryText,
                            fontSize = 10.sp, maxLines = 1, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GalleryCyan)
            }
            selected.isEmpty() -> Box(Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(if (tab == 3) "Nema favorita" else "Nema spremljenih LENSIGO snimki",
                    color = GalleryMuted, fontSize = 14.sp)
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(horizontal = 17.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(selected, key = { it.uri.toString() }) { item ->
                    val image by produceState<Bitmap?>(null, item.uri) {
                        value = withContext(Dispatchers.IO) {
                            MediaLibrary.thumbnail(context, item, 320)
                        }
                    }
                    Box(Modifier.aspectRatio(.72f)
                        .background(Color(0xFF153247), RoundedCornerShape(9.dp))
                        .clickable { onOpen(item) }) {
                        if (image != null) Image(image!!.asImageBitmap(), null,
                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        if (item.isVideo) Icon(Icons.Default.PlayArrow, "Video",
                            tint = GalleryText,
                            modifier = Modifier.align(Alignment.TopEnd).padding(5.dp))
                        if (MediaActions.isFavorite(context, item.uri)) {
                            Icon(Icons.Default.Favorite, "Favorit", tint = GalleryCyan,
                                modifier = Modifier.align(Alignment.BottomEnd)
                                    .padding(6.dp).size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewerScreen(
    item: LensMedia, quality: Int, lowMemory: Boolean,
    onBack: () -> Unit, onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editing by remember(item.uri) { mutableStateOf(false) }
    var tool by remember { mutableIntStateOf(0) }
    var bitmap by remember(item.uri) { mutableStateOf<Bitmap?>(null) }
    var busy by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var favorite by remember(item.uri) {
        mutableStateOf(MediaActions.isFavorite(context, item.uri))
    }
    var strength by remember { mutableFloatStateOf(.85f) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var chosen by remember { mutableStateOf(LensFilter.ORIGINAL) }
    var rotation by remember { mutableIntStateOf(0) }
    var crop by remember { mutableIntStateOf(0) }
    val cropAspects = listOf(null, 1f, 4f / 3f, 3f / 2f, 9f / 16f)
    val cropNames = listOf("Izvorno", "1:1", "4:3", "3:2", "9:16")
    val look = PhotoLook(filter = chosen, intensity = strength,
        brightness = brightness, contrast = contrast, saturation = saturation)

    LaunchedEffect(item.uri) {
        bitmap = withContext(Dispatchers.IO) {
            MediaLibrary.thumbnail(context, item, 1600)
        }
    }
    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) onBack()
        else Toast.makeText(context, "Brisanje otkazano", Toast.LENGTH_SHORT).show()
    }
    fun share() {
        try {
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = if (item.isVideo) "video/mp4" else "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, item.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Podijeli snimku"))
        } catch (_: Exception) {
            Toast.makeText(context, "Dijeljenje nije dostupno", Toast.LENGTH_LONG).show()
        }
    }
    fun delete() {
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                val request = MediaStore.createDeleteRequest(context.contentResolver, listOf(item.uri))
                deleteLauncher.launch(IntentSenderRequest.Builder(request.intentSender).build())
            } else {
                context.contentResolver.delete(item.uri, null, null)
                onBack()
            }
        } catch (e: android.app.RecoverableSecurityException) {
            if (Build.VERSION.SDK_INT >= 29) deleteLauncher.launch(
                IntentSenderRequest.Builder(e.userAction.actionIntent.intentSender).build())
        } catch (e: Exception) {
            Toast.makeText(context, "Brisanje nije moguće: " + e.localizedMessage,
                Toast.LENGTH_LONG).show()
        }
    }
    if (confirmDelete) AlertDialog(
        onDismissRequest = { confirmDelete = false },
        title = { Text("Izbrisati snimku?") },
        text = { Text("Datoteka će biti izbrisana iz galerije uređaja.") },
        confirmButton = { TextButton(onClick = { confirmDelete = false; delete() }) {
            Text("Izbriši")
        } },
        dismissButton = { TextButton(onClick = { confirmDelete = false }) {
            Text("Odustani")
        } }
    )
    Column(Modifier.fillMaxSize().background(if (editing) Color(0xFF0D2133) else Color.Black)) {
        Row(Modifier.fillMaxWidth().height(61.dp).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (editing) editing = false else onBack() }) {
                Icon(Icons.Default.ArrowBack, "Natrag", tint = GalleryText)
            }
            Text(if (editing) "Uređivanje" else if (item.isVideo) "Videozapis" else "Fotografija",
                color = GalleryText, fontSize = 18.sp,
                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f))
            IconButton(onClick = {
                favorite = MediaActions.toggleFavorite(context, item.uri)
            }) { Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                "Favorit", tint = if (favorite) GalleryCyan else GalleryText) }
        }
        if (editing) Column(Modifier.padding(horizontal = 18.dp, vertical = 15.dp)) {
            Text("Uredi fotografiju", fontWeight = FontWeight.Bold,
                fontSize = 27.sp, color = GalleryText)
            Text("Prilagodi svaki detalj kadra.", color = GalleryMuted, fontSize = 14.sp)
        }
        Box(Modifier.weight(1f).fillMaxWidth()
            .padding(horizontal = if (editing) 18.dp else 0.dp),
            contentAlignment = Alignment.Center) {
            val img = bitmap
            if (img != null) AndroidView(
                factory = { ctx -> ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                } },
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer(rotationZ = if (editing) rotation.toFloat() else 0f),
                update = {
                    it.setImageBitmap(img)
                    it.colorFilter = if (!editing || look.isNeutral) null
                        else FilterEngine.colorFilter(look)
                }
            ) else CircularProgressIndicator(color = GalleryCyan)
            if (item.isVideo) {
                Button(onClick = {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(item.uri, "video/mp4")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }) } catch (_: Exception) {
                        Toast.makeText(context, "Reproduktor nije dostupan", Toast.LENGTH_SHORT).show()
                    }
                }) { Icon(Icons.Default.PlayArrow, null); Text("Reproduciraj") }
            }
        }
        if (editing) {
            Row(Modifier.fillMaxWidth().height(74.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Svjetlina","Kontrast","Izreži","Filteri","Rotacija").forEachIndexed { index, name ->
                    Column(Modifier.weight(1f).clickable { tool = index }
                        .padding(vertical = 9.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(when (index) {
                            0 -> Icons.Default.Tune
                            1 -> Icons.Default.WbSunny
                            2 -> Icons.Default.Crop
                            3 -> Icons.Default.AutoAwesome
                            else -> Icons.Default.RotateRight
                        }, name, tint = if (tool == index) GalleryCyan else GalleryMuted,
                            modifier = Modifier.size(22.dp))
                        Spacer(Modifier.height(6.dp))
                        Text(name, fontSize = 10.sp, maxLines = 1,
                            color = if (tool == index) GalleryCyan else GalleryMuted)
                    }
                }
            }
            Column(Modifier.fillMaxWidth().height(106.dp)
                .padding(horizontal = 18.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                when (tool) {
                    0 -> {
                        Text("Svjetlina " + (brightness * 100).toInt(), color = GalleryText)
                        Slider(brightness, { brightness = it }, valueRange = -1f..1f)
                    }
                    1 -> {
                        Text("Kontrast " + (contrast * 100).toInt() + "%", color = GalleryText)
                        Slider(contrast, { contrast = it }, valueRange = .5f..1.8f)
                    }
                    2 -> LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        items(cropNames.size) { index ->
                            FilterChip(selected = crop == index, onClick = { crop = index },
                                label = { Text(cropNames[index]) })
                        }
                    }
                    3 -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            items(LensFilter.entries.size) { index ->
                                val selected = LensFilter.entries[index]
                                FilterChip(selected = chosen == selected,
                                    onClick = { chosen = selected },
                                    label = { Text(selected.title) })
                            }
                        }
                        Slider(strength, { strength = it },
                            modifier = Modifier.height(24.dp))
                    }
                    else -> Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rotacija " + rotation + "°", color = GalleryText)
                        Spacer(Modifier.width(16.dp))
                        OutlinedButton(onClick = { rotation = (rotation + 90) % 360 }) {
                            Text("+90°")
                        }
                    }
                }
            }
            Button(
                onClick = {
                    busy = true
                    scope.launch {
                        try {
                            MediaActions.saveEdited(context, item.uri, look, cropAspects[crop],
                                rotation, quality, lowMemory)
                            onSaved()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Pogreška: " + e.localizedMessage,
                                Toast.LENGTH_LONG).show()
                        } finally { busy = false }
                    }
                }, enabled = !busy,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                    .padding(bottom = 16.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D83AB)),
                shape = RoundedCornerShape(12.dp)
            ) { Text(if (busy) "Spremanje…" else "Spremi promjene", color = GalleryText) }
        } else {
            Row(Modifier.fillMaxWidth().height(124.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                BottomAction("Uredi", Icons.Default.Edit, !item.isVideo) { editing = true }
                BottomAction("Podijeli", Icons.Default.Share) { share() }
                BottomAction("Spremi", Icons.Default.FileDownload, !busy) {
                    busy = true
                    scope.launch {
                        try {
                            MediaActions.saveCopy(context, item)
                            Toast.makeText(context, "Kopija spremljena",
                                Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Pogreška spremanja: " + e.localizedMessage,
                                Toast.LENGTH_LONG).show()
                        } finally { busy = false }
                    }
                }
                BottomAction("Izbriši", Icons.Default.Delete) { confirmDelete = true }
            }
        }
    }
}

@Composable
private fun BottomAction(name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true, onClick: () -> Unit
) {
    Column(Modifier.clickable(enabled = enabled, onClick = onClick).padding(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, name, tint = if (enabled) GalleryText else GalleryMuted.copy(alpha = .35f),
            modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(10.dp))
        Text(name, color = if (enabled) GalleryText else GalleryMuted.copy(alpha = .35f),
            fontSize = 12.sp)
    }
}
