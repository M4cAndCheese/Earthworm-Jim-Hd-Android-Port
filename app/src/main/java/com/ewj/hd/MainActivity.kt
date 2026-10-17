package com.ewj.hd

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import android.content.pm.ActivityInfo

class MainActivity : Activity() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var overlay: TouchOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Use the cutout area on the short edges so the game is not clipped by the punch-hole.
        window.attributes = window.attributes.apply {
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        hideSystemBars()

        val root = FrameLayout(this)
        surfaceView = SurfaceView(this)
        overlay = TouchOverlay(this)
        root.addView(surfaceView)
        root.addView(overlay)
        setContentView(root)

        overlay.onPadChanged = { lx, ly, buttons -> NativeBridge.setPad(lx, ly, buttons) }
        overlay.onPause = { NativeBridge.setPause() }

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                NativeBridge.start(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                val vp = AspectViewport.compute(width, height)
                NativeBridge.setViewport(vp.x, vp.y, vp.w, vp.h)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                NativeBridge.stop()
            }
        })
    }

    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        }
    }
}
