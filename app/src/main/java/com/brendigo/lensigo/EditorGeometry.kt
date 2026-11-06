package com.brendigo.lensigo

/**
 * Platform-independent geometry. The viewer and saved-file pipeline use this
 * exact crop rectangle so the same aspect presets produce the same framing.
 */
data class CropArea(val left: Int, val top: Int, val width: Int, val height: Int)

object EditorGeometry {
    fun centerCrop(width: Int, height: Int, aspect: Float?): CropArea {
        require(width > 0 && height > 0) { "Image dimensions must be positive" }
        if (aspect == null || !aspect.isFinite() || aspect <= 0f)
            return CropArea(0, 0, width, height)
        val wider = width.toFloat() / height > aspect
        val cropWidth = if (wider) (height * aspect).toInt().coerceIn(1, width) else width
        val cropHeight = if (wider) height else (width / aspect).toInt().coerceIn(1, height)
        return CropArea((width - cropWidth) / 2, (height - cropHeight) / 2,
            cropWidth, cropHeight)
    }
}
