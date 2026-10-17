package com.ewj.hd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sqrt

/**
 * On-screen controls: a virtual stick at bottom-left, three action buttons at
 * bottom-right and a pause button at top-right, laid out like the reference
 * screenshot. Multi-touch aware: each finger is tracked by its pointer id.
 */
class TouchOverlay(context: Context) : View(context) {

    var onPadChanged: ((lx: Float, ly: Float, buttons: Int) -> Unit)? = null
    var onPause: (() -> Unit)? = null

    private class ActionButton(val label: String, val bit: Int, val fx: Float, val fy: Float) {
        var cx = 0f
        var cy = 0f
    }

    private val actionButtons = listOf(
        ActionButton("A", Pad.BTN_A, 0.88f, 0.84f),
        ActionButton("B", Pad.BTN_B, 0.76f, 0.66f),
        ActionButton("X", Pad.BTN_X, 0.88f, 0.48f),
    )

    private var stickCx = 0f
    private var stickCy = 0f
    private var stickR = 0f
    private var pauseCx = 0f
    private var pauseCy = 0f
    private var pauseR = 0f
    private var btnR = 0f

    private var stickPointer = -1
    private var lx = 0f
    private var ly = 0f
    private var mask = 0
    private val pointerButtons = HashMap<Int, Int>() // pointerId -> button bit

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = 0x88FFFFFF.toInt()
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x33FFFFFF
    }
    private val pressedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x66FFFFFF
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
        textSize = 40f
        isFakeBoldText = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val s = min(w, h).toFloat()
        stickCx = w * 0.12f
        stickCy = h * 0.70f
        stickR = s * 0.16f
        pauseCx = w * 0.93f
        pauseCy = h * 0.10f
        pauseR = s * 0.06f
        btnR = s * 0.09f
        actionButtons.forEach {
            it.cx = w * it.fx
            it.cy = h * it.fy
        }
    }

    private fun buttonAt(x: Float, y: Float): ActionButton? =
        actionButtons.firstOrNull { hypot(x - it.cx, y - it.cy) <= btnR * 1.1f }

    private fun inStick(x: Float, y: Float) = hypot(x - stickCx, y - stickCy) <= stickR * 1.6f

    private fun inPause(x: Float, y: Float) = hypot(x - pauseCx, y - pauseCy) <= pauseR * 1.5f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val i = e.actionIndex
                onDown(e.getPointerId(i), e.getX(i), e.getY(i))
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until e.pointerCount) {
                    if (e.getPointerId(i) == stickPointer) setStick(e.getX(i), e.getY(i))
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                onUp(e.getPointerId(e.actionIndex))
            }
            MotionEvent.ACTION_CANCEL -> {
                stickPointer = -1
                pointerButtons.clear()
                lx = 0f
                ly = 0f
                notifyPad()
            }
        }
        return true
    }

    private fun onDown(id: Int, x: Float, y: Float) {
        if (inPause(x, y)) {
            onPause?.invoke()
            return
        }
        val b = buttonAt(x, y)
        if (b != null) {
            pointerButtons[id] = b.bit
            recomputeMask()
            notifyPad()
            return
        }
        if (stickPointer == -1 && inStick(x, y)) {
            stickPointer = id
            setStick(x, y)
        }
    }

    private fun onUp(id: Int) {
        if (id == stickPointer) {
            stickPointer = -1
            lx = 0f
            ly = 0f
        }
        if (pointerButtons.remove(id) != null) recomputeMask()
        notifyPad()
    }

    private fun setStick(x: Float, y: Float) {
        val dx = (x - stickCx) / stickR
        val dy = (y - stickCy) / stickR
        val mag = sqrt(dx * dx + dy * dy)
        val scale = if (mag > 1f) 1f / mag else 1f
        val nx = dx * scale
        val ny = dy * scale
        if (mag < DEADZONE) {
            lx = 0f
            ly = 0f
        } else {
            lx = nx
            ly = -ny // screen y grows downward; game y grows upward
        }
        notifyPad()
    }

    private fun recomputeMask() {
        mask = pointerButtons.values.fold(0) { acc, bit -> acc or bit }
    }

    private fun notifyPad() {
        onPadChanged?.invoke(lx, ly, mask)
        invalidate()
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)

        // Stick
        c.drawCircle(stickCx, stickCy, stickR, fillPaint)
        c.drawCircle(stickCx, stickCy, stickR, ringPaint)
        c.drawCircle(
            stickCx + lx * stickR * 0.5f,
            stickCy - ly * stickR * 0.5f,
            stickR * 0.4f,
            pressedPaint,
        )

        // Action buttons
        for (b in actionButtons) {
            val pressed = (mask and b.bit) != 0
            c.drawCircle(b.cx, b.cy, btnR, if (pressed) pressedPaint else fillPaint)
            c.drawCircle(b.cx, b.cy, btnR, ringPaint)
            c.drawText(b.label, b.cx, b.cy + textPaint.textSize / 3f, textPaint)
        }

        // Pause (two bars)
        c.drawCircle(pauseCx, pauseCy, pauseR, fillPaint)
        c.drawCircle(pauseCx, pauseCy, pauseR, ringPaint)
        val bw = pauseR * 0.22f
        val bh = pauseR * 0.9f
        c.drawRect(pauseCx - bw * 1.6f, pauseCy - bh / 2, pauseCx - bw * 0.6f, pauseCy + bh / 2, textPaint)
        c.drawRect(pauseCx + bw * 0.6f, pauseCy - bh / 2, pauseCx + bw * 1.6f, pauseCy + bh / 2, textPaint)
    }

    companion object {
        private const val DEADZONE = 0.15f
    }
}
