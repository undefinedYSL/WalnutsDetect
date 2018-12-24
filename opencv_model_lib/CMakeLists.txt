# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
#opencv配置
#set(OPENCV_INSTALL_MODULES "ON")
#set(OPENCV_CAMERA_MODULES "ON")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/src/main/sdk/native/jni)
include_directories(${OpenCV_DIR}/include)
include_directories(${OpenCV_DIR}/include/opencv)
include_directories(${OpenCV_DIR}/include/opencv2)

find_package(OpenCV REQUIRED)
#end opencv----------------------------------------------------------------------

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
if(EXISTS ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libtbb.a)
add_library(tbb STATIC IMPORTED)
endif()

if(EXISTS ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libtbb.a)
set_target_properties(tbb PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libtbb.a)
endif()
add_library( # Sets the name of the library.
            MyCppNative

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
              src/main/cpp/DealImage.cpp
             src/main/cpp/native-lib.cpp
              )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              MyCppNative

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
if(EXISTS ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libtbb.a)
  target_link_libraries( # Specifies the target library.
                         MyCppNative
                         jnigraphics
                          tbb
                              ${OpenCV_LIBS}
                         # Links the target library to the log library
                         # included in the NDK.
                         ${log-lib}
                         ${jnigraphics-lib})
else()
  target_link_libraries( # Specifies the target library.
                       MyCppNative
                       jnigraphics
                        ${OpenCV_LIBS}
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       ${jnigraphics-lib})
endif()

