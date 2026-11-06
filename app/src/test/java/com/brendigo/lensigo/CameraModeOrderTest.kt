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
            cameraModeStrip.drop(cameraModeScrollIndex(CameraMode.VIDEO)).take(5)
        )
    }

    @Test
    fun photoReferenceShowsFiltersVideoPhotoProNightFromSecondStripItem() {
        assertEquals(
            listOf(CameraMode.FILTERS, CameraMode.VIDEO, CameraMode.PHOTO,
                CameraMode.PRO, CameraMode.NIGHT),
            cameraModeStrip.drop(cameraModeScrollIndex(CameraMode.PHOTO)).take(5)
        )
    }

    @Test
    fun panoramaAndDualReferencesShowWrappedModeNeighbors() {
        assertEquals(
            listOf(CameraMode.NIGHT, CameraMode.PORTRAIT, CameraMode.PANORAMA,
                CameraMode.DUAL, CameraMode.FILTERS),
            cameraModeStrip.drop(cameraModeScrollIndex(CameraMode.PANORAMA)).take(5)
        )
        assertEquals(
            listOf(CameraMode.PORTRAIT, CameraMode.PANORAMA, CameraMode.DUAL,
                CameraMode.FILTERS, CameraMode.VIDEO),
            cameraModeStrip.drop(cameraModeScrollIndex(CameraMode.DUAL)).take(5)
        )
    }

    @Test
    fun everyModeHasFiveVisibleNeighbors() {
        CameraMode.entries.forEach { mode ->
            assertEquals(5, cameraModeStrip.drop(cameraModeScrollIndex(mode)).take(5).size)
            assertTrue(cameraModeStrip.drop(cameraModeScrollIndex(mode)).take(5).contains(mode))
        }
    }

    @Test
    fun allCameraModesArePresentExactlyOnce() {
        assertEquals(CameraMode.entries.size, cameraModeOrder.size)
        assertEquals(CameraMode.entries.toSet(), cameraModeOrder.toSet())
        assertTrue(cameraModeOrder.indexOf(CameraMode.PHOTO) >= 2)
    }
}
