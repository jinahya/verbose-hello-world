# Builds every C program in this directory, one executable per *.c file.
#
#   cmake -S src/test/c -B target/c-cmake
#   cmake --build target/c-cmake
#
# Cross-platform alternative to the module Makefile; works with any CMake generator (Unix Makefiles,
# Ninja, Visual Studio, Xcode, ...).

cmake_minimum_required(VERSION 3.21)

project(verbose_hello_world_c LANGUAGES C)

# C23, for the *_WIDTH, BITINT_MAXWIDTH, and _FloatN rows; decays to the newest supported standard
# where C23 is not available (e.g., MSVC), and the programs #ifdef-guard every optional row.
set(CMAKE_C_STANDARD 23)
set(CMAKE_C_EXTENSIONS OFF)

# libm, where it is a separate library
find_library(MATH_LIBRARY m)

file(GLOB sources CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/*.c")
foreach (source IN LISTS sources)
    get_filename_component(name "${source}" NAME_WE)
    add_executable("${name}" "${source}")
    if (MSVC)
        target_compile_options("${name}" PRIVATE /W4)
    else ()
        target_compile_options("${name}" PRIVATE -Wall -Wextra)
    endif ()
    if (MATH_LIBRARY)
        target_link_libraries("${name}" PRIVATE "${MATH_LIBRARY}")
    endif ()
endforeach ()
