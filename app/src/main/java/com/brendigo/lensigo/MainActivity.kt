package com.brendigo.lensigo

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.PendingRecording
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF06121E)
private val Cyan = Color(0xFF00D4FF)
private val Muted = Color(0xFF9DB9C9)
private enum class Mode(val title: String) {
    PHOTO("FOTO"), VIDEO("VIDEO"), PRO("PRO"), NIGHT("NOĆ"),
    PORTRAIT("PORTRET"), PANORAMA("PANORAMA"), DUAL("DUAL"), FILTERS("FILTERI")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        setContent { LensigoApp(
            showGallery = {
                val i = Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivity(i)
            }
        ) }
    }
}

@Composable
private fun LensigoApp(showGallery: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    var mode by remember { mutableStateOf(Mode.PHOTO) }
    var front by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    var granted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val request = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        granted = it[Manifest.permission.CAMERA] == true
    }
    LaunchedEffect(Unit) {
        if (!granted) request.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
    }
    var preview by remember { mutableStateOf<PreviewView?>(null) }
    var photo by remember { mutableStateOf<ImageCapture?>(null) }
    var video by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    DisposableEffect(Unit) {
        onDispose { recording?.stop(); cameraProvider?.unbindAll() }
    }

    LaunchedEffect(preview, front, granted) {
        val pv = preview ?: return@LaunchedEffect
        if (!granted) return@LaunchedEffect
        try {
            val provider = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context).get()
            cameraProvider = provider
            val p = Preview.Builder().build().also { it.surfaceProvider = pv.surfaceProvider }
            val capture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
            val vid = VideoCapture.withOutput(Recorder.Builder().build())
            val selector = if (front) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            provider.unbindAll()
            provider.bindToLifecycle(lifecycle, selector, p, capture, vid)
            photo = capture
            video = vid
        } catch (error: Exception) {
            Toast.makeText(context, "Kamera nije dostupna: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun takePhoto() {
        val capture = photo ?: return
        val filename = "LENSIGO_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= 29) put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/LENSIGO")
        }
        val output = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        ).build()
        capture.takePicture(output, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Fotografija spremljena", Toast.LENGTH_SHORT).show()
            }
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Pogreška: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun toggleVideo() {
        if (isRecording) {
            recording?.stop()
            recording = null
            isRecording = false
            return
        }
        val vc = video ?: return
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "LENSIGO_" + System.currentTimeMillis() + ".mp4")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= 29) put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/LENSIGO")
        }
        val options = MediaStoreOutputOptions.Builder(
            context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(values).build()
        val pending: PendingRecording = vc.output.prepareRecording(context, options)
        val withAudio = if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) pending.withAudioEnabled() else pending
        recording = withAudio.start(ContextCompat.getMainExecutor(context)) { event ->
            if (event is VideoRecordEvent.Finalize) {
                isRecording = false
                recording = null
                val message = if (event.hasError()) "Pogreška snimanja: ${event.error}" else "Videozapis spremljen"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        isRecording = true
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, background = Ink, surface = Ink)) {
        Column(Modifier.fillMaxSize().background(Ink)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LENSIGO", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("PRO CAMERA", fontSize = 11.sp, color = Cyan)
                TextButton(onClick = { settings = !settings }) { Text(if (settings) "ZATVORI" else "⚙", color = Color.White) }
            }
            if (settings) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Postavke", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("LENSIGO · by Brendigo", color = Cyan)
                    Text("Fotografije: Pictures/LENSIGO", color = Color.White)
                    Text("Videozapisi: Movies/LENSIGO", color = Color.White)
                    Text("Privatnost: fotografije se snimaju lokalno na uređaju.", color = Muted)
                    Text("Verzija 0.1.0 — početna funkcionalna verzija.", color = Muted)
                    Button(onClick = showGallery) { Text("Otvori galeriju") }
                }
                Spacer(Modifier.weight(1f))
            } else {
                Box(Modifier.weight(1f).fillMaxWidth().background(Color.Black)) {
                    if (granted) {
                        AndroidView(
                            factory = { ctx -> PreviewView(ctx).also { preview = it } },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Za rad kamere potrebna je dozvola", color = Color.White, textAlign = TextAlign.Center)
                            Button(onClick = { request.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }) { Text("Dopusti kameru") }
                        }
                    }
                    Text("LENSIGO", Modifier.align(Alignment.TopCenter).padding(top = 14.dp), color = Color.White.copy(alpha = .65f), fontSize = 12.sp)
                }
                LazyRow(
                    Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp)
                ) {
                    items(Mode.entries) { item ->
                        Text(
                            item.title,
                            modifier = Modifier.clickable {
                                if (isRecording) return@clickable
                                mode = item
                                if (item !in listOf(Mode.PHOTO, Mode.VIDEO)) {
                                    Toast.makeText(context, "${item.title}: uskoro", Toast.LENGTH_SHORT).show()
                                }
                            },
                            color = if (mode == item) Cyan else Muted,
                            fontWeight = if (mode == item) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 28.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = showGallery) { Text("GALERIJA", color = Color.White, fontSize = 12.sp) }
                    Box(
                        Modifier.size(76.dp)
                            .border(3.dp, Color.White, CircleShape).padding(6.dp)
                            .background(if (mode == Mode.VIDEO) Color(0xFFFF3B42) else Color.White, CircleShape)
                            .clickable {
                                when (mode) {
                                    Mode.PHOTO -> takePhoto()
                                    Mode.VIDEO -> toggleVideo()
                                    else -> Toast.makeText(context, "Način je u razvoju", Toast.LENGTH_SHORT).show()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRecording) Box(Modifier.size(24.dp).background(Color.White, RoundedCornerShape(5.dp)))
                    }
                    TextButton(onClick = { if (!isRecording) front = !front }) {
                        Text("↻", color = Color.White, fontSize = 33.sp)
                    }
                }
            }
        }
    }
}