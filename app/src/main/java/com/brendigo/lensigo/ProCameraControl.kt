package com.brendigo.lensigo

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.util.Range
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera

/** All manual options are hardware-gated. No simulated ISO or shutter settings. */
data class ProCapabilities(
    val manualSensor: Boolean = false,
    val iso: Range<Int>? = null,
    val shutterNanos: Range<Long>? = null,
    val focusDistance: Float = 0f,
    val whiteBalanceModes: List<Int> = listOf(CaptureRequest.CONTROL_AWB_MODE_AUTO)
)

data class ProValues(
    val manualExposure: Boolean = false,
    val iso: Int = 100,
    val shutterNanos: Long = 8_000_000L,
    val whiteBalanceMode: Int = CaptureRequest.CONTROL_AWB_MODE_AUTO,
    val manualFocus: Boolean = false,
    val focusDiopters: Float = 0f
)

object ProCameraControl {
    @OptIn(ExperimentalCamera2Interop::class)
    fun capabilities(camera: Camera): ProCapabilities {
        return try {
            val info = Camera2CameraInfo.from(camera.cameraInfo)
            val available = info.getCameraCharacteristic(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.toList() ?: emptyList()
            val iso = info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
            val exposure = info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
            val modes = info.getCameraCharacteristic(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)
                ?.toList() ?: listOf(CaptureRequest.CONTROL_AWB_MODE_AUTO)
            val lens = info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
                ?: 0f
            ProCapabilities(
                manualSensor = available.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR) &&
                    iso != null && exposure != null,
                iso = iso, shutterNanos = exposure, focusDistance = lens,
                whiteBalanceModes = modes
            )
        } catch (_: Exception) { ProCapabilities() }
    }

    val wbPresets = listOf(
        CaptureRequest.CONTROL_AWB_MODE_AUTO to "AUTO",
        CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT to "DAN",
        CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT to "OBLAČNO",
        CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT to "ŽARULJA",
        CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT to "FLUO"
    )

    val shutterPresets = listOf(
        1_000_000L to "1/1000",
        2_000_000L to "1/500",
        4_000_000L to "1/250",
        8_000_000L to "1/125",
        16_666_667L to "1/60",
        33_333_333L to "1/30",
        66_666_667L to "1/15"
    )

    @OptIn(ExperimentalCamera2Interop::class)
    fun apply(camera: Camera, capabilities: ProCapabilities, values: ProValues) {
        val builder = CaptureRequestOptions.Builder()
        if (values.manualExposure && capabilities.manualSensor) {
            val isoRange = capabilities.iso ?: return
            val timeRange = capabilities.shutterNanos ?: return
            builder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_OFF)
            builder.setCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY,
                values.iso.coerceIn(isoRange.lower, isoRange.upper))
            builder.setCaptureRequestOption(CaptureRequest.SENSOR_EXPOSURE_TIME,
                values.shutterNanos.coerceIn(timeRange.lower, timeRange.upper))
        }
        val wb = if (capabilities.whiteBalanceModes.contains(values.whiteBalanceMode))
            values.whiteBalanceMode else CaptureRequest.CONTROL_AWB_MODE_AUTO
        builder.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_MODE, wb)
        if (values.manualFocus && capabilities.focusDistance > 0f) {
            builder.setCaptureRequestOption(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_OFF)
            builder.setCaptureRequestOption(CaptureRequest.LENS_FOCUS_DISTANCE,
                values.focusDiopters.coerceIn(0f, capabilities.focusDistance))
        }
        Camera2CameraControl.from(camera.cameraControl).setCaptureRequestOptions(builder.build())
    }
}
