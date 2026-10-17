# ewj-game-private

Repo privado con los datos del juego y el codegen de ReXGlue. Se usa junto con el
port público: https://github.com/M4cAndCheese/Earthworm-Jim-Hd-Android-Port

## Workflows
- `codegen.yml`: descarga los datos del juego (release `game-data-v1`), genera el código
  y lo sube como artifact `ewj-generated-code`. Sólo comprueba que el codegen funciona.
- `build-game-apk.yml`: hace el codegen, copia el código generado en
  `app/src/main/cpp/generated/` del port y compila el APK jugable
  (artifact `EarthwormJimHD-game-debug-apk`). Se puede lanzar a mano y elegir la
  rama o tag del port (`port_ref`, por defecto `main`).

El APK no incluye los datos del juego; se copian al dispositivo como indica el README del port.
