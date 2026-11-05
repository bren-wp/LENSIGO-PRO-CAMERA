package com.brendigo.lensigo

import android.hardware.camera2.CaptureRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import kotlin.math.abs

private val PanelNavy = Color(0xFF081724)
private val PanelCyan = Color(0xFF00D4FF)
private val PanelWhite = Color(0xFFF9FCFF)
private val PanelMuted = Color(0xFF93ADBE)
private enum class Setting { ISO, SHUTTER, EV, WB, FOCUS }

@Composable
fun ProControlOverlay(
    capabilities: ProCapabilities?,
    values: ProValues,
    onValues: (ProValues) -> Unit,
    exposure: Int,
    minExposure: Int,
    maxExposure: Int,
    onExposure: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(Setting.EV) }
    val caps = capabilities ?: ProCapabilities()
    val availableShutters = ProCameraControl.shutterPresets.filter {
        it.first >= (caps.shutterNanos?.lower ?: 0L) &&
            it.first <= (caps.shutterNanos?.upper ?: Long.MAX_VALUE)
    }
    val wb = ProCameraControl.wbPresets.filter { caps.whiteBalanceModes.contains(it.first) }
    val shutterLabel = ProCameraControl.shutterPresets.minByOrNull {
        abs(it.first - values.shutterNanos)
    }?.second ?: "AUTO"
    val shutterDisplay = if (values.manualExposure && caps.manualSensor) shutterLabel else "AUTO"
    val isoDisplay = if (values.manualExposure && caps.manualSensor)
        values.iso.toString() else "AUTO"
    val focusDisplay = if (values.manualFocus && caps.focusDistance > 0f)
        String.format(java.util.Locale.US, "%.1f", values.focusDiopters) else "AUTO"
    val wbDisplay = wb.firstOrNull { it.first == values.whiteBalanceMode }?.second ?: "AUTO"
    Column(modifier.background(Color.Black.copy(alpha = .86f), RoundedCornerShape(12.dp))
        .padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(
                "S" to shutterDisplay, "EV" to exposure.toString(), "WB" to wbDisplay
            ).forEach { (label, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PanelWhite)
                    Text(value, fontSize = 12.sp, color = PanelWhite)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            .background(PanelNavy, RoundedCornerShape(10.dp)).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(
                Triple(Setting.ISO, "ISO", isoDisplay),
                Triple(Setting.SHUTTER, "S", shutterDisplay),
                Triple(Setting.EV, "EV", exposure.toString()),
                Triple(Setting.WB, "WB", wbDisplay),
                Triple(Setting.FOCUS, "MF", focusDisplay)
            ).forEach { (setting, label, value) ->
                val supported = when(setting) {
                    Setting.ISO, Setting.SHUTTER -> caps.manualSensor
                    Setting.EV -> maxExposure > minExposure
                    Setting.WB -> wb.size > 1
                    Setting.FOCUS -> caps.focusDistance > 0f
                }
                Column(
                    Modifier.weight(1f).clickable(enabled = supported) {
                        selected = setting
                        if (setting == Setting.EV && values.manualExposure)
                            onValues(values.copy(manualExposure = false))
                    }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(label, fontSize = 11.sp, color = if (supported) PanelMuted else PanelMuted.copy(alpha = .4f))
                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (!supported) PanelMuted.copy(alpha = .45f)
                        else if (setting == selected) PanelCyan else PanelWhite,
                        textAlign = TextAlign.Center, maxLines = 1)
                }
            }
        }
        when (selected) {
            Setting.ISO -> {
                val range = caps.iso
                if (caps.manualSensor && range != null && range.upper > range.lower) {
                    Slider(
                        value = values.iso.coerceIn(range.lower, range.upper).toFloat(),
                        onValueChange = {
                            onValues(values.copy(manualExposure = true,
                                iso = it.toInt().coerceIn(range.lower, range.upper)))
                        },
                        valueRange = range.lower.toFloat()..range.upper.toFloat(),
                        modifier = Modifier.padding(horizontal = 12.dp).height(36.dp))
                } else NoControl("Ručni ISO nije podržan")
            }
            Setting.SHUTTER -> {
                if (caps.manualSensor && availableShutters.isNotEmpty()) {
                    val selectedIndex = availableShutters.indexOfFirst { it.first == values.shutterNanos }
                        .takeIf { it >= 0 } ?: availableShutters.indices.minByOrNull {
                        abs(availableShutters[it].first - values.shutterNanos)
                    } ?: 0
                    Slider(value = selectedIndex.toFloat(),
                        onValueChange = {
                            val i = it.toInt().coerceIn(0, availableShutters.lastIndex)
                            onValues(values.copy(manualExposure = true,
                                shutterNanos = availableShutters[i].first))
                        }, valueRange = 0f..availableShutters.lastIndex.coerceAtLeast(1).toFloat(),
                        steps = (availableShutters.size - 2).coerceAtLeast(0),
                        modifier = Modifier.padding(horizontal = 12.dp).height(36.dp))
                } else NoControl("Brzina zatvarača nije podržana")
            }
            Setting.EV -> {
                if (maxExposure > minExposure) {
                    Slider(value = exposure.coerceIn(minExposure,maxExposure).toFloat(),
                        onValueChange = { onExposure(it.toInt().coerceIn(minExposure,maxExposure)) },
                        valueRange = minExposure.toFloat()..maxExposure.toFloat(),
                        steps = (maxExposure - minExposure - 1).coerceAtLeast(0),
                        modifier = Modifier.padding(horizontal = 12.dp).height(36.dp))
                } else NoControl("EV kompenzacija nije podržana")
            }
            Setting.WB -> {
                if (wb.size > 1) {
                    val index = wb.indexOfFirst { it.first == values.whiteBalanceMode }.coerceAtLeast(0)
                    Slider(value = index.toFloat(), onValueChange = {
                        onValues(values.copy(whiteBalanceMode =
                            wb[it.toInt().coerceIn(0,wb.lastIndex)].first))
                    }, valueRange = 0f..wb.lastIndex.toFloat(), steps = (wb.size-2).coerceAtLeast(0),
                        modifier = Modifier.padding(horizontal = 12.dp).height(36.dp))
                } else NoControl("Balans bijele nije podržan")
            }
            Setting.FOCUS -> {
                if (caps.focusDistance > 0f) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { onValues(values.copy(manualFocus = !values.manualFocus)) }) {
                            Text(if (values.manualFocus) "MF" else "AF", color = PanelCyan)
                        }
                        Slider(
                            value = values.focusDiopters.coerceIn(0f,caps.focusDistance),
                            onValueChange = {
                                onValues(values.copy(manualFocus = true,
                                    focusDiopters = it))
                            }, valueRange = 0f..caps.focusDistance,
                            modifier = Modifier.weight(1f).height(36.dp)
                        )
                    }
                } else NoControl("Ručni fokus nije podržan")
            }
        }
    }
}

@Composable
private fun NoControl(message: String) {
    Text(message, color = PanelMuted, fontSize = 11.sp, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(9.dp))
}
