package com.brendigo.lensigo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun GalleryScreen(onBack: () -> Unit, onOpen: (LensMedia) -> Unit) {
    val context = LocalContext.current
    var items by remember { mutableStateOf<List<LensMedia>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        items = withContext(Dispatchers.IO) { MediaLibrary.list(context) }
        isLoading = false
    }
    Column(Modifier.fillMaxSize().background(GalleryInk)) {
        Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Natrag", tint = GalleryText) }
            Text("Galerija", color = GalleryText, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(items.size.toString() + " stavki", color = GalleryMuted,
                fontSize = 12.sp, modifier = Modifier.padding(end = 16.dp))
        }
        Row(Modifier.padding(horizontal = 16.dp, vertical = 7.dp)) {
            Text("SVE", color = GalleryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(20.dp))
            Text("Fotografije i videozapisi LENSIGO", color = GalleryMuted, fontSize = 12.sp)
        }
        when {
            isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = GalleryCyan)
            items.isEmpty() -> Column(Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Nema spremljenih snimki", color = GalleryText, fontSize = 18.sp)
                Text("Fotografiraj ili snimi video da se pojavi u galeriji.",
                    color = GalleryMuted)
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                contentPadding = PaddingValues(3.dp)
            ) {
                items(items, key = { it.uri.toString() }) { item ->
                    val thumb by produceState<Bitmap?>(null, item.uri) {
                        value = withContext(Dispatchers.IO) { MediaLibrary.thumbnail(context, item) }
                    }
                    Box(Modifier.aspectRatio(1f).background(Color(0xFF13283C))
                        .clickable { onOpen(item) }) {
                        if (thumb != null) Image(thumb!!.asImageBitmap(), null,
                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        if (item.isVideo) {
                            Icon(Icons.Default.PlayArrow, "Video",
                                tint = GalleryText, modifier = Modifier.align(Alignment.BottomEnd)
                                    .padding(7.dp).background(GalleryInk.copy(alpha = .65f),
                                        RoundedCornerShape(16.dp)).padding(3.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewerScreen(item: LensMedia, quality: Int, lowMemory: Boolean,
                 onBack: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bitmap by remember(item.uri) { mutableStateOf<Bitmap?>(null) }
    var saving by remember { mutableStateOf(false) }
    var chosen by remember { mutableStateOf(LensFilter.ORIGINAL) }
    var strength by remember { mutableFloatStateOf(.85f) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    val look = PhotoLook(filter = chosen, intensity = strength,
        brightness = brightness, contrast = contrast, saturation = saturation)
    LaunchedEffect(item.uri) {
        bitmap = withContext(Dispatchers.IO) { MediaLibrary.thumbnail(context, item, 960) }
    }
    fun share() {
        try {
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = if (item.isVideo) "video/mp4" else "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, item.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Podijeli snimku"))
        } catch (e: Exception) {
            Toast.makeText(context, "Dijeljenje nije dostupno", Toast.LENGTH_SHORT).show()
        }
    }
    Column(Modifier.fillMaxSize().background(GalleryInk)) {
        Row(Modifier.fillMaxWidth().height(60.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Natrag", tint = GalleryText) }
            Text(if (item.isVideo) "Videozapis" else "Uredi fotografiju", color = GalleryText,
                fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = ::share) { Icon(Icons.Default.Share, "Podijeli", tint = GalleryText) }
        }
        Box(Modifier.weight(1f).fillMaxWidth().background(Color.Black),
            contentAlignment = Alignment.Center) {
            val img = bitmap
            if (img != null) AndroidView(
                factory = { ctx -> ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                } },
                modifier = Modifier.fillMaxSize(),
                update = {
                    it.setImageBitmap(img)
                    it.colorFilter = if (item.isVideo || look.isNeutral) null else FilterEngine.colorFilter(look)
                }
            )
            else CircularProgressIndicator(color = GalleryCyan)
            if (item.isVideo) {
                Button(onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(item.uri, "video/mp4")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        })
                    } catch (_: Exception) {
                        Toast.makeText(context, "Nema reproduktora videa", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(7.dp))
                    Text("Reproduciraj")
                }
            }
        }
        if (!item.isVideo) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(LensFilter.entries.size) { index ->
                    val filter = LensFilter.entries[index]
                    FilterChip(selected = chosen == filter,
                        onClick = { chosen = filter },
                        label = { Text(filter.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GalleryCyan, selectedLabelColor = GalleryInk,
                            containerColor = Color(0xFF153247), labelColor = GalleryText
                        ))
                }
            }
            Text("Jačina filtra", color = GalleryMuted, fontSize = 12.sp,
                modifier = Modifier.padding(start = 18.dp))
            Slider(value = strength, onValueChange = { strength = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(30.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Svjetlina", color = GalleryMuted, fontSize = 11.sp)
                    Slider(brightness, { brightness = it }, valueRange = -1f..1f)
                }
                Column(Modifier.weight(1f)) {
                    Text("Kontrast", color = GalleryMuted, fontSize = 11.sp)
                    Slider(contrast, { contrast = it }, valueRange = .5f..1.8f)
                }
                Column(Modifier.weight(1f)) {
                    Text("Boja", color = GalleryMuted, fontSize = 11.sp)
                    Slider(saturation, { saturation = it }, valueRange = 0f..2f)
                }
            }
            Button(enabled = !saving, onClick = {
                saving = true
                scope.launch {
                    try {
                        MediaLibrary.saveEdited(context, item.uri, look, quality, lowMemory)
                        onSaved()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Uređivanje nije uspjelo: " + e.localizedMessage,
                            Toast.LENGTH_LONG).show()
                    } finally { saving = false }
                }
            }, modifier = Modifier.fillMaxWidth().padding(start = 16.dp,
                end = 16.dp, bottom = 12.dp)) {
                Text(if (saving) "Spremanje…" else "Spremi uređenu kopiju")
            }
        }
    }
}
