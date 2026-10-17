#!/bin/sh
# Copies SDL's Java glue (org.libsdl.app) for the SDL version the ReXGlue SDK ships.
# The SDK's libSDL3.a is 3.5.0, so the Java side must match it.
set -e
TAG=release-3.5.0
DEST=app/src/main/java/org/libsdl/app
TMP=$(mktemp -d)
curl -fL -o "$TMP/sdl.tar.gz" "https://github.com/libsdl-org/SDL/archive/refs/tags/$TAG.tar.gz"
tar -xzf "$TMP/sdl.tar.gz" -C "$TMP"
SRC=$(ls -d "$TMP"/SDL-*/android-project/app/src/main/java/org/libsdl/app)
mkdir -p "$DEST"
cp "$SRC"/*.java "$DEST/"
cp "$SRC"/*.txt "$DEST/" 2>/dev/null || true
echo "SDL $TAG Java glue: $(ls "$DEST" | wc -l) files"
