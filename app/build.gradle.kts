plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Paths the private build passes in. Defaults work for the public CI.
val reXSdkDir = System.getenv("REXSDK_ANDROID_DIR") ?: "${rootProject.projectDir}/sdk-android"
val rexglueHostTool = System.getenv("REXGLUE_HOST_TOOL") ?: ""

android {
    namespace = "com.ewj.hd"
    compileSdk = 35
    ndkVersion = "28.2.13676358"

    defaultConfig {
        applicationId = "com.ewj.hd"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.2.0"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
        externalNativeBuild {
            cmake {
                arguments += listOf(
                    "-DEWJ_SDK_DIR=$reXSdkDir",
                    "-DREXGLUE_HOST_TOOL=$rexglueHostTool",
                    "-DANDROID_STL=c++_shared",
                )
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.31.6"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    packaging {
        // The runtime finds its GPU plugin next to librexruntime.so and dlopens it by path.
        jniLibs {
            useLegacyPackaging = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

// ReXGlue runtime libraries, staged by tools/stage-sdk.sh.
android.sourceSets.getByName("main").jniLibs.srcDir("libs")
