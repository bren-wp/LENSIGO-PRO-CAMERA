package com.brendigo.lensigo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraModeOrderTest {
    @Test
    fun photoReferenceShowsFiltersVideoPhotoProNightInOrder() {
        assertEquals(
            listOf(
                CameraMode.FILTERS, CameraMode.VIDEO, CameraMode.PHOTO,
                CameraMode.PRO, CameraMode.NIGHT
            ),
            cameraModeOrder.take(5)
        )
    }

    @Test
    fun videoReferenceShowsDualFiltersVideoPhotoPro() {
        assertEquals(
            listOf(CameraMode.DUAL, CameraMode.FILTERS, CameraMode.VIDEO,
                CameraMode.PHOTO, CameraMode.PRO),
            cameraModeStrip.take(5)
        )
    }

    @Test
    fun photoReferenceShowsFiltersVideoPhotoProNightFromSecondStripItem() {
        assertEquals(
            listOf(CameraMode.FILTERS, CameraMode.VIDEO, CameraMode.PHOTO,
                CameraMode.PRO, CameraMode.NIGHT),
            cameraModeStrip.drop(1).take(5)
        )
    }

    @Test
    fun allCameraModesArePresentExactlyOnce() {
        assertEquals(CameraMode.entries.size, cameraModeOrder.size)
        assertEquals(CameraMode.entries.toSet(), cameraModeOrder.toSet())
        assertTrue(cameraModeOrder.indexOf(CameraMode.PHOTO) >= 2)
    }
}
