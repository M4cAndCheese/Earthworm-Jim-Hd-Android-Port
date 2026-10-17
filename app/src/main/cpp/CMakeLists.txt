cmake_minimum_required(VERSION 3.22.1)
project(ewj_android LANGUAGES C CXX)

# Native side of the Android app. Right now it is a stub that only receives
# input and viewport data from Kotlin. The ReXGlue runtime and the recompiled
# game code are added here once they exist for Android (see README_ANDROID.md).
find_library(log-lib log)
find_library(android-lib android)

add_library(ewj SHARED ewj_android.cpp)
target_link_libraries(ewj ${log-lib} ${android-lib})
