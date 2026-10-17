// Touch controls: the Java-side overlay (TouchOverlay) sends its state here, and it
// is fed to an SDL3 virtual gamepad. The runtime's SDL input driver picks the pad
// up, so the game sees an ordinary controller.
//
// EWJ_HAS_SDK is defined when the game is built (see CMakeLists.txt). Without it
// (no generated game code) these functions do nothing.
#include <jni.h>

#include <algorithm>
#include <chrono>
#include <mutex>
#include <thread>

#if defined(EWJ_HAS_SDK)
#include <SDL3/SDL.h>
#endif

namespace {

std::mutex g_mutex;

#if defined(EWJ_HAS_SDK)
SDL_Joystick* g_pad = nullptr;
SDL_JoystickID g_pad_id = 0;

// Must hold g_mutex. The joystick subsystem is started by the runtime, so the pad
// is attached lazily, once SDL is up.
bool EnsurePad() {
  if (g_pad) {
    return true;
  }
  if (!SDL_WasInit(SDL_INIT_JOYSTICK)) {
    return false;
  }
  SDL_VirtualJoystickDesc desc;
  SDL_INIT_INTERFACE(&desc);
  desc.type = SDL_JOYSTICK_TYPE_GAMEPAD;
  desc.naxes = SDL_GAMEPAD_AXIS_COUNT;
  desc.nbuttons = SDL_GAMEPAD_BUTTON_COUNT;
  desc.name = "Earthworm Jim touch controls";
  SDL_JoystickID id = SDL_AttachVirtualJoystick(&desc);
  if (!id) {
    return false;
  }
  g_pad = SDL_OpenJoystick(id);
  if (!g_pad) {
    SDL_DetachVirtualJoystick(id);
    return false;
  }
  g_pad_id = id;
  return true;
}

void SetButton(int button, bool down) {
  if (g_pad) {
    SDL_SetJoystickVirtualButton(g_pad, button, down);
  }
}

Sint16 ToAxis(float value) {
  return static_cast<Sint16>(std::clamp(value, -1.0f, 1.0f) * 32767.0f);
}
#endif

}  // namespace

extern "C" {

// lx, ly in [-1, 1] (ly positive = up). buttons: bit0 = A, bit1 = B, bit2 = X.
JNIEXPORT void JNICALL
Java_com_ewj_hd_NativeBridge_setPad(JNIEnv*, jobject, jfloat lx, jfloat ly, jint buttons) {
#if defined(EWJ_HAS_SDK)
  std::lock_guard lock(g_mutex);
  if (!EnsurePad()) {
    return;
  }
  SDL_SetJoystickVirtualAxis(g_pad, SDL_GAMEPAD_AXIS_LEFTX, ToAxis(lx));
  SDL_SetJoystickVirtualAxis(g_pad, SDL_GAMEPAD_AXIS_LEFTY, ToAxis(-ly));  // SDL: y grows down
  SetButton(SDL_GAMEPAD_BUTTON_SOUTH, (buttons & 1) != 0);  // A
  SetButton(SDL_GAMEPAD_BUTTON_EAST, (buttons & 2) != 0);   // B
  SetButton(SDL_GAMEPAD_BUTTON_WEST, (buttons & 4) != 0);   // X
#else
  (void)lx;
  (void)ly;
  (void)buttons;
#endif
}

// Presses Start for a moment (the game's pause).
JNIEXPORT void JNICALL
Java_com_ewj_hd_NativeBridge_setPause(JNIEnv*, jobject) {
#if defined(EWJ_HAS_SDK)
  std::thread([] {
    {
      std::lock_guard lock(g_mutex);
      if (!EnsurePad()) {
        return;
      }
      SetButton(SDL_GAMEPAD_BUTTON_START, true);
    }
    std::this_thread::sleep_for(std::chrono::milliseconds(120));
    std::lock_guard lock(g_mutex);
    SetButton(SDL_GAMEPAD_BUTTON_START, false);
  }).detach();
#endif
}

}  // extern "C"
