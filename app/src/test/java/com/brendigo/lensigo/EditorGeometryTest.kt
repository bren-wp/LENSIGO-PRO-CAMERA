package com.brendigo.lensigo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EditorGeometryTest {
    @Test
    fun landscapeSquareCropsHorizontalEdges() {
        assertEquals(CropArea(500, 0, 3000, 3000),
            EditorGeometry.centerCrop(4000, 3000, 1f))
    }

    @Test
    fun portraitLandscapeAspectCentersVertically() {
        assertEquals(CropArea(0, 1156, 3000, 1687),
            EditorGeometry.centerCrop(3000, 4000, 16f / 9f))
    }

    @Test
    fun originalPreservesPixelDimensions() {
        assertEquals(CropArea(0, 0, 1080, 2400),
            EditorGeometry.centerCrop(1080, 2400, null))
    }

    @Test
    fun invalidAspectFallsBackWithoutCreatingAZeroSizedCrop() {
        assertEquals(CropArea(0, 0, 17, 13),
            EditorGeometry.centerCrop(17, 13, Float.NaN))
        assertEquals(CropArea(0, 0, 17, 13),
            EditorGeometry.centerCrop(17, 13, 0f))
    }

    @Test
    fun rejectsInvalidImageDimensions() {
        assertThrows(IllegalArgumentException::class.java) {
            EditorGeometry.centerCrop(0, 20, 1f)
        }
    }
}
