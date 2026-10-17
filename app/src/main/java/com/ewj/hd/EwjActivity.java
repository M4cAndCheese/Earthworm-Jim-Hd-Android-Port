package com.ewj.hd;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.libsdl.app.SDLActivity;

/**
 * Runs Earthworm Jim HD. SDLActivity loads the libraries below and calls SDL_main()
 * in libewj.so (provided by the ReXGlue runtime). The game data is not in the APK:
 * it is read from /sdcard/Android/data/com.ewj.hd/files/game/ (see README_ANDROID.md).
 */
public class EwjActivity extends SDLActivity {

    @Override
    protected String[] getLibraries() {
        // Dependencies first, the game last.
        return new String[] {
            "c++_shared",
            "rexruntime",
            "ewj",
        };
    }

    @Override
    protected String getMainSharedObject() {
        return "libewj.so";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Touch controls on top of the game surface. They drive an SDL virtual gamepad.
        addContentView(new TouchOverlay(this), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
