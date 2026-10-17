#!/bin/sh
# Copies the ReXGlue runtime libraries and libc++_shared into app/libs/arm64-v8a.
# Usage: tools/stage-sdk.sh <extracted-android-sdk-dir> <ndk-dir>
set -e
SDK=$1
NDK=$2
DEST=app/libs/arm64-v8a
mkdir -p "$DEST"
cp "$SDK/lib/librexruntime.so" "$SDK/lib/librexgpu-xenos.so" "$SDK/lib/librexgpu-null.so" "$DEST/"
PREBUILT=$(ls -d "$NDK"/toolchains/llvm/prebuilt/*/ | head -n 1)
cp "$PREBUILT/sysroot/usr/lib/aarch64-linux-android/libc++_shared.so" "$DEST/"
ls -la "$DEST"
