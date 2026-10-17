cmake_minimum_required(VERSION 3.25)
project(ewj_native LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ccache: the generated game code (~480 files, ~140k functions) is the bulk of the
# build and is identical between runs unless the TOML files change. With a cache
# restored by CI, unchanged objects are reused instead of recompiled.
find_program(EWJ_CCACHE ccache)
if(EWJ_CCACHE)
  set(CMAKE_C_COMPILER_LAUNCHER "${EWJ_CCACHE}")
  set(CMAKE_CXX_COMPILER_LAUNCHER "${EWJ_CCACHE}")
  message(STATUS "ccache enabled: ${EWJ_CCACHE}")
endif()

# ReXGlue Android SDK: the artifact rexglue-android-arm64-sdk (release sdk-rexglue-0.10.0).
set(EWJ_SDK_DIR "" CACHE PATH "ReXGlue Android SDK install prefix (contains lib/cmake/rexglue)")
if(EWJ_SDK_DIR)
    list(APPEND CMAKE_PREFIX_PATH "${EWJ_SDK_DIR}")
endif()

# Generated game code (codegen in the private repo, copied here before the build).
set(EWJ_GENERATED_DIR "${CMAKE_CURRENT_SOURCE_DIR}/generated")
if(NOT EXISTS "${EWJ_GENERATED_DIR}/default/sources.cmake")
    message(STATUS "No generated game code: building the stub library (the game will not start)")
    add_library(ewj SHARED ewj_android.cpp)
    target_link_libraries(ewj PRIVATE log android)
    return()
endif()

if(EWJ_SDK_DIR)
    list(APPEND CMAKE_FIND_ROOT_PATH "${EWJ_SDK_DIR}")
    if(EXISTS "${EWJ_SDK_DIR}/lib/cmake/rexglue/rexglueConfig.cmake")
        set(rexglue_DIR "${EWJ_SDK_DIR}/lib/cmake/rexglue" CACHE PATH "ReXGlue SDK config dir")
    endif()
endif()
find_package(rexglue 0.10.0 CONFIG)

include("${EWJ_GENERATED_DIR}/rexglue.cmake")
include("${EWJ_GENERATED_DIR}/default/sources.cmake")

# The rexglue binary inside the Android SDK runs on the device, not on the build
# machine. Codegen needs the host one, so point the SDK's tool at it when given.
set(REXGLUE_HOST_TOOL "" CACHE FILEPATH "Host rexglue binary, used for codegen when cross-compiling")
if(REXGLUE_HOST_TOOL AND TARGET rex::rexglue)
    set_target_properties(rex::rexglue PROPERTIES
        IMPORTED_LOCATION_RELEASE "${REXGLUE_HOST_TOOL}"
        IMPORTED_LOCATION "${REXGLUE_HOST_TOOL}")
endif()

add_library(ewj SHARED ${GENERATED_SOURCES} ewj_android.cpp)
target_compile_definitions(ewj PRIVATE EWJ_HAS_SDK=1)
rexglue_setup_target(ewj GPU_PLUGINS xenos)
target_link_libraries(ewj PRIVATE log android)
