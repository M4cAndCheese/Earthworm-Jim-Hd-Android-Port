package com.ewj.hd

import kotlin.math.roundToInt

data class Viewport(val x: Int, val y: Int, val w: Int, val h: Int)

object AspectViewport {
    /**
     * Largest rectangle with the target aspect ratio that fits the screen,
     * centered. The game is never stretched; the leftover area stays black.
     * Used on 19.5:9 phones like the Poco X8 Pro Max (2772x1280 in landscape).
     */
    fun compute(screenW: Int, screenH: Int, targetAspect: Float = 16f / 9f): Viewport {
        val screenAspect = screenW.toFloat() / screenH.toFloat()
        return if (screenAspect > targetAspect) {
            val w = (screenH * targetAspect).roundToInt()
            Viewport((screenW - w) / 2, 0, w, screenH)
        } else {
            val h = (screenW / targetAspect).roundToInt()
            Viewport(0, (screenH - h) / 2, screenW, h)
        }
    }
}
