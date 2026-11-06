package com.brendigo.lensigo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoLookTest {
    @Test
    fun originalPhotoWithoutAdjustmentsIsNeutral() {
        assertTrue(PhotoLook().isNeutral)
    }

    @Test
    fun colorEffectRequiresImageProcessing() {
        assertFalse(PhotoLook(filter = LensFilter.VIVID).isNeutral)
        assertFalse(PhotoLook(brightness = .2f).isNeutral)
        assertFalse(PhotoLook(contrast = 1.2f).isNeutral)
        assertFalse(PhotoLook(saturation = .8f).isNeutral)
    }

    @Test
    fun optionalEnhancementsAreNotNeutral() {
        assertFalse(PhotoLook(lowLight = true).isNeutral)
        assertFalse(PhotoLook(softenNoise = true).isNeutral)
        assertFalse(PhotoLook(sharpen = true).isNeutral)
        assertFalse(PhotoLook(portraitBlur = .5f).isNeutral)
    }
}
