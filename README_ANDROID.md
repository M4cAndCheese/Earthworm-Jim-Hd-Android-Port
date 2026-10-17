# Earthworm Jim HD - Android

Port of Earthworm Jim HD (Xbox 360 XBLA) to Android with ReXGlue 0.10.0 (Vulkan),
on the Poco X8 Pro Max (Mali-G925).

## Layout
- `app/src/main/java/com/ewj/hd/EwjActivity.java`: `SDLActivity` runs the game.
- `app/src/main/java/com/ewj/hd/TouchOverlay.kt`: on-screen stick and buttons.
- `app/src/main/cpp/ewj_android.cpp`: touch feeds an SDL virtual gamepad.
- `app/src/main/cpp/CMakeLists.txt`: game mode when `generated/` exists, stub otherwise.
- `tools/fetch-sdl-java.sh`: SDL 3.5.0 Java glue (zlib), matching the SDK's SDL.
- `tools/stage-sdk.sh`: ReXGlue runtime libraries and libc++ into `app/libs/`.
- `sdk-patches/rexglue-android/`: the Android patches for ReXGlue (GPL-3.0).

## Build
- Public (no game): `.github/workflows/android.yml`, artifact `EarthwormJimHD-debug-apk`.
- With the game: `build-game-apk.yml` in the private repo, which runs codegen and
  copies `generated/` into `app/src/main/cpp/generated/`.

## Game data (not in the APK)
Copy your extracted game folder (the one with `default.xex`) to the device:

```sh
G=/sdcard/Android/data/com.ewj.hd/files/game
adb install -r app-debug.apk
adb shell mkdir -p $G
adb push game/. $G/
adb shell "find $G -type d -exec chmod 777 {} +"
```

The runtime log is `/sdcard/Android/data/com.ewj.hd/files/rayman.log` (the SDK
patch names it after Rayman; it is the same log).

## Status
- Control overlay: shown on the phone (verified on the Poco).
- Game: pending first run with generated code.

## License
GPL-3.0 (see `LICENSE`). Includes the GPL-3.0 ReXGlue Android patches. ReXGlue
v0.10.0 is BSD-3-Clause. SDL is zlib. The game is not included.
