package com.brendigo.lensigo

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Locale

/** Local operations for media created by LENSIGO. Never uploads files. */
object MediaActions {
    private const val FAVORITES = "lensigo_favorites"

    fun isFavorite(context: Context, uri: Uri): Boolean =
        context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE)
            .getStringSet("items", emptySet())?.contains(uri.toString()) == true

    fun toggleFavorite(context: Context, uri: Uri): Boolean {
        val prefs = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE)
        val values = prefs.getStringSet("items", emptySet()).orEmpty().toMutableSet()
        val added = if (values.contains(uri.toString())) {
            values.remove(uri.toString()); false
        } else { values.add(uri.toString()); true }
        prefs.edit().putStringSet("items", values).apply()
        return added
    }

    suspend fun saveCopy(context: Context, source: LensMedia): Uri =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val stamp = System.currentTimeMillis()
            val mime = if (source.isVideo) "video/mp4" else "image/jpeg"
            val uri = if (source.isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "LENSIGO_COPY_$stamp." +
                    if (source.isVideo) "mp4" else "jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, mime)
                if (Build.VERSION.SDK_INT >= 29) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH,
                        if (source.isVideo) "Movies/LENSIGO" else "Pictures/LENSIGO")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }
            val destination = resolver.insert(uri, values)
                ?: throw IOException("Nije moguće spremiti kopiju")
            try {
                val input = resolver.openInputStream(source.uri)
                    ?: throw IOException("Izvorna datoteka nije dostupna")
                input.use { ins ->
                    val out = resolver.openOutputStream(destination, "w")
                        ?: throw IOException("Nije moguće pisati kopiju")
                    out.use { ins.copyTo(it, bufferSize = 64 * 1024) }
                }
                if (Build.VERSION.SDK_INT >= 29) {
                    resolver.update(destination, ContentValues().apply {
                        put(MediaStore.MediaColumns.IS_PENDING, 0)
                    }, null, null)
                }
                destination
            } catch (e: Exception) {
                resolver.delete(destination, null, null)
                throw e
            }
        }

    /** Crop and rotate offline. Always writes a new item, never replaces the source. */
    suspend fun saveEdited(context: Context, source: Uri, look: PhotoLook,
                           cropAspect: Float?, rotation: Int,
                           quality: Int, lowMemory: Boolean): Uri =
        withContext(Dispatchers.IO) {
            val original = File.createTempFile("lensigo_editor_", ".jpg", context.cacheDir)
            var result: Bitmap? = null
            try {
                context.contentResolver.openInputStream(source)?.use { input ->
                    original.outputStream().use { input.copyTo(it) }
                } ?: throw IOException("Fotografija nije dostupna")
                result = FilterEngine.decode(original, if (lowMemory) 2048 else 3072)
                if (cropAspect != null && cropAspect > 0) {
                    val w = result.width; val h = result.height
                    val currentAspect = w.toFloat() / h
                    val cropW = if (currentAspect > cropAspect)
                        (h * cropAspect).toInt().coerceIn(1, w) else w
                    val cropH = if (currentAspect > cropAspect) h
                        else (w / cropAspect).toInt().coerceIn(1, h)
                    val crop = Bitmap.createBitmap(result, (w - cropW) / 2,
                        (h - cropH) / 2, cropW, cropH)
                    if (crop !== result) result.recycle()
                    result = crop
                }
                if (rotation % 360 != 0) {
                    val rotated = Bitmap.createBitmap(result, 0, 0,
                        result.width, result.height,
                        Matrix().apply { postRotate(rotation.toFloat()) }, true)
                    if (rotated !== result) result.recycle()
                    result = rotated
                }
                val filtered = FilterEngine.apply(result, look)
                if (filtered !== result) result.recycle()
                result = filtered
                MediaLibrary.saveBitmap(context, result, "EDIT", quality)
            } finally {
                result?.recycle()
                original.delete()
            }
        }
}
