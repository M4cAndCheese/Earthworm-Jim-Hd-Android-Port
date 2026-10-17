# Earthworm Jim HD - Android (scaffold)

Estado: esqueleto del proyecto. Compila como app Android con el nombre
"Earthworm Jim HD", pero NO ejecuta el juego todavía. El motor (ReXGlue
para Android + código recompilado) no existe en este repo.

## Dispositivo objetivo
Poco X8 Pro Max: MediaTek Dimensity 9500s, GPU Immortalis-G925 (Mali,
Valhall), Android 16, pantalla 6.83" 1280x2772 19.5:9 a 120 Hz.

Implicaciones:
- arm64-v8a únicamente (configurado en app/build.gradle.kts).
- minSdk 29 (Android 10+).
- No usa el driver Turnip de Adreno. En Mali se usa el driver Vulkan del
  sistema. El port de referencia (UnleashedRecomp-Android) lo marca como
  experimental, con texturas BC decodificadas en CPU por falta de soporte.
- Pantalla 19.5:9: AspectViewport calcula un 16:9 centrado sin estirar.

## Controles
Según la captura de referencia: stick virtual abajo a la izquierda,
botones A/B/X a la derecha y pausa arriba a la derecha. Los botones
llevan bits en Pad.kt; hay que confirmar el mapeo contra el input real
del juego antes de usarlo.

## Archivos
- settings.gradle.kts, build.gradle.kts: proyecto Gradle (AGP 8.7.3, Kotlin 2.0.21).
- app/build.gradle.kts: applicationId com.ewj.hd, arm64-v8a, CMake.
- app/src/main/AndroidManifest.xml: landscape sensor, fullscreen, sin recorte en punch-hole.
- app/src/main/java/com/ewj/hd/: MainActivity, TouchOverlay, NativeBridge, AspectViewport.
- app/src/main/cpp/: stub nativo con JNI (start, stop, setPad, setViewport, setPause).

## Compilar con GitHub Actions
El workflow `.github/workflows/android.yml` compila el APK en cada push,
pull request o desde la pestaña Actions (workflow_dispatch). El APK aparece
como artifact "EarthwormJimHD-debug-apk". No hace falta Android Studio.
Usa JDK 17, NDK 27.0.12077973 y CMake 3.22.1, igual que `app/build.gradle.kts`.

## Pendiente
1. Primera ejecución del workflow: el fallo de setup-android ya se corrigió (se usa sdkmanager del runner).
2. Motor: el workflow `rexglue-android-sdk.yml` compila ReXGlue v0.10.0 con los parches de Rayman
   (`sdk-patches/rexglue-android/`, GPL-3.0) para arm64. Falta verificar el primer build y enlazarlo en CMakeLists.txt.
3. Datos del juego: el paquete XBLA del usuario, instalado en el almacenamiento de la app.
4. Icono: ya usa el icono de la captura del XBLA (mipmap-*/ic_launcher.png).
5. Prueba en el Poco X8 Pro Max con log de Logcat.

## Verificación
Este scaffold se escribió sin poder compilarlo en el entorno: no hay SDK
ni NDK disponibles. La primera compilación real ocurre en GitHub Actions.
