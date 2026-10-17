#!/bin/sh
# Copies SDL's Java glue (org.libsdl.app) for the exact SDL commit the ReXGlue v0.10.0
# SDK is built against (thirdparty/sdl3 submodule). That commit is a 3.5.0 development
# snapshot and has no release tag, so the Java side is taken by commit SHA.
# Keep SDL_COMMIT in sync with the SDK's thirdparty/sdl3 submodule.
set -e
SDL_COMMIT=8bf3b7215ad9fc3deb583c6a3a37c6c67f2e24e4
DEST=app/src/main/java/org/libsdl/app
TMP=$(mktemp -d)
curl -fL -o "$TMP/sdl.tar.gz" "https://github.com/libsdl-org/SDL/archive/$SDL_COMMIT.tar.gz"
tar -xzf "$TMP/sdl.tar.gz" -C "$TMP"
SRC=$(ls -d "$TMP"/SDL-*/android-project/app/src/main/java/org/libsdl/app)
mkdir -p "$DEST"
cp "$SRC"/*.java "$DEST/"
cp "$SRC"/*.txt "$DEST/" 2>/dev/null || true
echo "SDL $SDL_COMMIT Java glue: $(ls "$DEST" | wc -l) files"
