package com.brendigo.lensigo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeLayoutRulesTest {
    @Test fun mobileTilesMatchSourceAndFitFourRows() {
        assertEquals(2, HomeLayoutRules.columns(360f, 1f))
        assertEquals(76, HomeLayoutRules.tileHeightDp(false, 1f))
        assertEquals(146, HomeLayoutRules.bannerHeightDp(false, 1f))
        assertEquals(334, 4 * HomeLayoutRules.tileHeightDp(false, 1f) + 3 * 10)
    }
    @Test fun tabletTilesMatchTwoSourceRows() {
        assertEquals(4, HomeLayoutRules.columns(800f, 1f))
        assertEquals(131, HomeLayoutRules.tileHeightDp(true, 1f))
        assertEquals(218, HomeLayoutRules.bannerHeightDp(true, 1f))
    }
    @Test fun largeTextHasAccessibleFallback() {
        assertEquals(1, HomeLayoutRules.columns(320f, 1f))
        assertEquals(1, HomeLayoutRules.columns(360f, 1.6f))
        assertEquals(2, HomeLayoutRules.columns(800f, 2f))
        assertTrue(HomeLayoutRules.tileHeightDp(false, 2f) > 76)
    }
}
