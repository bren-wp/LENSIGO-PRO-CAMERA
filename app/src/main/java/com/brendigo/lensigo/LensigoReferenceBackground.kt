package com.brendigo.lensigo

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Source-measured backdrop shared by Gallery, Editor, Settings, Privacy and
 * About. Sampled from the user-supplied 1080x2400 references (13–19):
 * y≈100 #0E2134, y≈700 #0E2235, y≈1000 #0E2134, y≈2320 #08121F.
 *
 * Stop positions are normalized to the Composable height so the same
 * proportions apply on tablets. The exact device pixel raster is still
 * subject to density, insets and renderer checks.
 */
internal val LensigoReferenceBackground = Brush.verticalGradient(
    0f to Color(0xFF0E2134),
    .30f to Color(0xFF0E2235),
    .44f to Color(0xFF0E2134),
    1f to Color(0xFF08121F)
)
