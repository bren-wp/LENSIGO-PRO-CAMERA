package com.brendigo.lensigo

/** 04_home reference geometry, measured from the supplied phone/tablet PNGs. */
internal object HomeLayoutRules {
    fun columns(widthDp: Float, fontScale: Float): Int = when {
        widthDp >= 600f && fontScale >= 1.6f -> 2
        widthDp >= 600f -> 4
        widthDp < 340f || (widthDp < 420f && fontScale >= 1.6f) -> 1
        else -> 2
    }

    fun tileHeightDp(tablet: Boolean, fontScale: Float): Int = when {
        tablet && fontScale >= 1.6f -> 166
        tablet -> 131
        fontScale >= 2f -> 116
        fontScale >= 1.6f -> 104
        fontScale >= 1.2f -> 92
        else -> 76
    }

    fun bannerHeightDp(tablet: Boolean, fontScale: Float): Int = when {
        tablet && fontScale >= 1.6f -> 285
        tablet && fontScale >= 1.2f -> 244
        tablet -> 218
        fontScale >= 2f -> 300
        fontScale >= 1.6f -> 264
        fontScale >= 1.2f -> 200
        else -> 146
    }
}
