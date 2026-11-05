package com.brendigo.lensigo

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.net.Uri
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException

data class LensMedia(val uri: Uri, val isVideo: Boolean, val timestamp: Long)

object MediaLibrary {
    fun list(context: Context): List<LensMedia> {
        val result = mutableListOf<LensMedia>()
        val sources = listOf(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI to false,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI to true
        )
        for ((source, video) in sources) {
            val date = MediaStore.MediaColumns.DATE_ADDED
            val name = MediaStore.MediaColumns.DISPLAY_NAME
            val projection = arrayOf(MediaStore.MediaColumns._ID, date, name)
            try {
                context.contentResolver.query(source, projection,
                    name + " LIKE ?", arrayOf("LENSIGO_%"), date + " DESC")
                    ?.use { cursor ->
                        val id = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                        val time = cursor.getColumnIndexOrThrow(date)
                        while (cursor.moveToNext()) {
                            result.add(LensMedia(ContentUris.withAppendedId(source, cursor.getLong(id)),
                                video, cursor.getLong(time)))
                        }
                    }
            } catch (_: SecurityException) { /* Permission denied: show owned items only. */ }
        }
        return result.sortedByDescending { it.timestamp }
    }

    fun thumbnail(context: Context, item: LensMedia, size: Int = 240): Bitmap? {
        if (Build.VERSION.SDK_INT >= 29) return try {
            context.contentResolver.loadThumbnail(item.uri, Size(size, size), CancellationSignal())
        } catch (_: Exception) { null }
        if (item.isVideo) {
            val retriever = MediaMetadataRetriever()
            return try {
                retriever.setDataSource(context, item.uri)
                retriever.getFrameAtTime(0)
            } catch (_: Exception) { null } finally { retriever.release() }
        }
        return try {
            context.contentResolver.openInputStream(item.uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, BitmapFactory.Options().apply { inSampleSize = 4 })
            }
        } catch (_: Exception) { null }
    }

    fun saveImage(context: Context, file: File, look: PhotoLook,
                  quality: Int, lowMemory: Boolean): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "LENSIGO_" + System.currentTimeMillis() + ".jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/LENSIGO")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("Nije moguće kreirati fotografiju")
        try {
            resolver.openOutputStream(uri, "w")?.use { output ->
                if (look.isNeutral) {
                    FileInputStream(file).use { input -> input.copyTo(output) }
                } else {
                    val bitmap = FilterEngine.decode(file, if (lowMemory) 2048 else 3072)
                    val edited = try { FilterEngine.apply(bitmap, look) } finally { bitmap.recycle() }
                    try {
                        if (!edited.compress(Bitmap.CompressFormat.JPEG, quality, output)) {
                            throw IOException("JPEG kodiranje nije uspjelo")
                        }
                    } finally { edited.recycle() }
                }
            } ?: throw IOException("Nije moguće pisati fotografiju")
            if (Build.VERSION.SDK_INT >= 29) {
                resolver.update(uri, ContentValues().apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 0)
                }, null, null)
            }
            return uri
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    suspend fun saveEdited(context: Context, uri: Uri,
                           look: PhotoLook, quality: Int, lowMemory: Boolean): Uri =
        withContext(Dispatchers.IO) {
            val file = File.createTempFile("lensigo_edit_", ".jpg", context.cacheDir)
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                } ?: throw IOException("Datoteka nije dostupna")
                saveImage(context, file, look, quality, lowMemory)
            } finally { file.delete() }
        }
}
