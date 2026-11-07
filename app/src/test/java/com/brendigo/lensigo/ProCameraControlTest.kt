package com.brendigo.lensigo

import android.hardware.camera2.CaptureRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProCameraControlTest {
    @Test fun useContinuousAfOnlyWhenCameraSupportsIt() {
        val continuous = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        val auto = CaptureRequest.CONTROL_AF_MODE_AUTO
        val off = CaptureRequest.CONTROL_AF_MODE_OFF
        assertEquals(continuous, ProCameraControl.automaticFocusMode(listOf(auto, continuous)))
        assertEquals(auto, ProCameraControl.automaticFocusMode(listOf(auto)))
        assertEquals(off, ProCameraControl.automaticFocusMode(listOf(off)))
        assertNull(ProCameraControl.automaticFocusMode(emptyList()))
    }
}
