package com.ewj.hd

/** Button bits sent to the native side. Native maps: bit0 = A, bit1 = B, bit2 = X. */
object Pad {
    const val BTN_A = 1 shl 0
    const val BTN_B = 1 shl 1
    const val BTN_X = 1 shl 2
}

object NativeBridge {
    init {
        System.loadLibrary("ewj")
    }

    /** lx, ly in [-1, 1]; ly is positive when the stick points up. */
    external fun setPad(lx: Float, ly: Float, buttons: Int)

    /** Sends Start for a moment, which the game uses for pause. */
    external fun setPause()
}
