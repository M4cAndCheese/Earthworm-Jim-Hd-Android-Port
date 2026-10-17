package com.ewj.hd

import android.view.Surface

/** Button bits sent to the native side. Check them against the game's input mapping. */
object Pad {
    const val BTN_A = 1 shl 0
    const val BTN_B = 1 shl 1
    const val BTN_X = 1 shl 2
}

object NativeBridge {
    init {
        System.loadLibrary("ewj")
    }

    external fun start(surface: Surface): Boolean
    external fun stop()
    /** lx, ly in [-1, 1]; ly is positive when the stick points up. */
    external fun setPad(lx: Float, ly: Float, buttons: Int)
    /** Game area in surface pixels, already letterboxed to 16:9. */
    external fun setViewport(x: Int, y: Int, w: Int, h: Int)
    external fun setPause()
}
