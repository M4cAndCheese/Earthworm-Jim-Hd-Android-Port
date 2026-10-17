#include <jni.h>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <atomic>

#define LOG_TAG "EWJ"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

namespace {
std::atomic<float> g_lx{0.0f};
std::atomic<float> g_ly{0.0f};
std::atomic<int> g_buttons{0};
std::atomic<int> g_vp_x{0}, g_vp_y{0}, g_vp_w{0}, g_vp_h{0};
ANativeWindow* g_window = nullptr;
}  // namespace

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_ewj_hd_NativeBridge_start(JNIEnv* env, jobject, jobject surface) {
    g_window = ANativeWindow_fromSurface(env, surface);
    LOGI("start: window=%p", static_cast<void*>(g_window));
    // TODO: create the Vulkan surface and start the game runtime here.
    return g_window ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_ewj_hd_NativeBridge_stop(JNIEnv*, jobject) {
    if (g_window) {
        ANativeWindow_release(g_window);
        g_window = nullptr;
    }
    LOGI("stop");
}

JNIEXPORT void JNICALL
Java_com_ewj_hd_NativeBridge_setPad(JNIEnv*, jobject, jfloat lx, jfloat ly, jint buttons) {
    g_lx.store(lx);
    g_ly.store(ly);
    g_buttons.store(buttons);
}

JNIEXPORT void JNICALL
Java_com_ewj_hd_NativeBridge_setViewport(JNIEnv*, jobject, jint x, jint y, jint w, jint h) {
    g_vp_x.store(x);
    g_vp_y.store(y);
    g_vp_w.store(w);
    g_vp_h.store(h);
}

JNIEXPORT void JNICALL
Java_com_ewj_hd_NativeBridge_setPause(JNIEnv*, jobject) {
    LOGI("pause requested");
}

}  // extern "C"
