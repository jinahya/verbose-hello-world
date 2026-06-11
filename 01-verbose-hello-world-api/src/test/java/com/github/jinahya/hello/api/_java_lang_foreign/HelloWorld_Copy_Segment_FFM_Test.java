package com.github.jinahya.hello.api._java_lang_foreign;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.api.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.lang.foreign.*;
import java.nio.charset.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link HelloWorld#copy(MemorySegment)} with various native libraries via FFM API. Tests are
 * conditionally skipped based on OS and library availability.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("copy(segment) via FFM")
class HelloWorld_Copy_Segment_FFM_Test {

    // ----------------------------------------------------------------------------------- Utilities

    private static boolean isLibraryAvailable(final String... names) {
        try (var arena = Arena.ofConfined()) {
            return findLibrary(arena, names).isPresent();
        }
    }

    private static Optional<SymbolLookup> findLibrary(final Arena arena, final String... names) {
        for (final var name : names) {
            try {
                return Optional.of(SymbolLookup.libraryLookup(name, arena));
            } catch (final Exception e) {
                // Try next
            }
        }
        return Optional.empty();
    }

    /**
     * Reads the first {@value HelloWorld#BYTES} bytes from the segment as a US-ASCII string.
     *
     * @param segment the segment to read from.
     * @return the string content.
     */
    private static String readSegmentAsString(final MemorySegment segment) {
        final var bytes = new byte[HelloWorld.BYTES];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = segment.get(ValueLayout.JAVA_BYTE, i);
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    private static void stubSetArrayWillCopyHelloWorldBytes(final HelloWorld service) {
        doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            final var bytes = "hello, world".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(bytes, 0, array, 0, bytes.length);
            return array;
        }).when(service).set(any(byte[].class));
    }

    // -------------------------------------------------------------------------------------- C/libc

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} with C standard library (libc) functions.
     * <p>
     * <b>libc</b> is the C standard library providing core functions for C programs.
     * It is available on all POSIX systems (Linux, macOS, BSD) and Windows (via MSVCRT).
     *
     * <h2>Functions Used</h2>
     * <ul>
     *   <li>{@code puts(const char *s)} - Writes string {@code s} and a newline to stdout.
     *       Returns non-negative on success, EOF on error.</li>
     *   <li>{@code strlen(const char *s)} - Returns the length of string {@code s},
     *       not including the null terminator.</li>
     *   <li>{@code memcmp(const void *s1, const void *s2, size_t n)} - Compares first
     *       {@code n} bytes of {@code s1} and {@code s2}. Returns 0 if identical.</li>
     * </ul>
     *
     * @see <a href="https://en.cppreference.com/w/c/io/puts">puts - cppreference.com</a>
     * @see <a href="https://en.cppreference.com/w/c/string/byte/strlen">strlen -
     * cppreference.com</a>
     * @see <a href="https://en.cppreference.com/w/c/string/byte/memcmp">memcmp -
     * cppreference.com</a>
     */
    @DisplayName("libc")
    @Nested
    class Libc_Test
            extends HelloWorld__Test {

        /**
         * Verifies segment content using C {@code puts()}.
         * <p>
         * {@code int puts(const char *s)} writes the string {@code s} and a trailing newline to
         * {@code stdout}. Requires null-terminated string.
         */
        @DisplayName("should verify the <segment> content through C <puts>")
        @Test
        void _puts__()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            try (var arena = Arena.ofConfined()) {
                // +1 for null terminator (puts requires null-terminated string)
                final var segment = arena.allocate(HelloWorld.BYTES + 1);
                // ----------------------------------------------------------------------------when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                final var array = HelloWorld__TestUtils.set_array12_invoked_once(service);
                final var content = readSegmentAsString(segment);
                assertEquals("hello, world", content);
                final var linker = Linker.nativeLinker();
                final var puts = linker.downcallHandle(
                        linker.defaultLookup().find("puts").orElseThrow(),
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
                );
                puts.invoke(segment);  // prints "hello, world\n"
            }
        }

        /**
         * Verifies segment content using C {@code strlen()}.
         * <p>
         * {@code size_t strlen(const char *s)} returns the number of bytes in string {@code s}, not
         * counting the terminating null character.
         */
        @DisplayName("should verify the <segment> length through C <strlen>")
        @Test
        void _strlen__()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            try (var arena = Arena.ofConfined()) {
                final var segment = arena.allocate(HelloWorld.BYTES + 1);
                // ---------------------------------------------------------------------------- when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                final var linker = Linker.nativeLinker();
                final var strlen = linker.downcallHandle(
                        linker.defaultLookup().find("strlen").orElseThrow(),
                        FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
                );
                final var len = (long) strlen.invoke(segment);
                assertEquals(HelloWorld.BYTES, len);
            }
        }

        /**
         * Verifies segment content using C {@code memcmp()}.
         * <p>
         * {@code int memcmp(const void *s1, const void *s2, size_t n)} compares the first {@code n}
         * bytes of memory areas {@code s1} and {@code s2}. Returns 0 if they are equal.
         */
        @DisplayName("should verify the <segment> content through C <memcmp>")
        @Test
        void _memcmp__()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            try (var arena = Arena.ofConfined()) {
                final var segment = arena.allocate(HelloWorld.BYTES);
                // ---------------------------------------------------------------------------- when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                final var linker = Linker.nativeLinker();
                final var memcmp = linker.downcallHandle(
                        linker.defaultLookup().find("memcmp").orElseThrow(),
                        FunctionDescriptor.of(
                                ValueLayout.JAVA_INT,
                                ValueLayout.ADDRESS,
                                ValueLayout.ADDRESS,
                                ValueLayout.JAVA_LONG
                        )
                );
                final var expected = arena.allocateFrom("hello, world", StandardCharsets.US_ASCII);
                final var result = (int) memcmp.invoke(segment, expected, (long) HelloWorld.BYTES);
                assertEquals(0, result, "Memory content should match 'hello, world'");
            }
        }
    }

    // ------------------------------------------------------------------------------------- Python

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} with the Python interpreter via its C API.
     * <p>
     * <b>Python C API</b> allows embedding Python in C/C++ applications. The shared library
     * ({@code libpython3.so}, {@code libpython3.dylib}, or {@code python3.dll}) provides functions
     * to initialize the interpreter and execute Python code.
     *
     * <h2>Functions Used</h2>
     * <ul>
     *   <li>{@code Py_Initialize()} - Initializes the Python interpreter. Must be called
     *       before any other Python C API functions (except a few).</li>
     *   <li>{@code PyRun_SimpleString(const char *command)} - Executes Python source code
     *       from a null-terminated string. Returns 0 on success, -1 on error.</li>
     *   <li>{@code Py_Finalize()} - Shuts down the Python interpreter and frees resources.</li>
     * </ul>
     *
     * @see <a href="https://docs.python.org/3/c-api/init.html#c.Py_Initialize">Py_Initialize</a>
     * @see <a
     * href="https://docs.python.org/3/c-api/veryhigh.html#c.PyRun_SimpleString">PyRun_SimpleString</a>
     * @see <a href="https://docs.python.org/3/c-api/init.html#c.Py_Finalize">Py_Finalize</a>
     */
    @DisplayName("python")
    @Nested
    class Python_Test
            extends HelloWorld__Test {

        private static final List<String> PYTHON_LIBS_MACOS = List.of(
                // Homebrew Cellar paths (versioned)
                "/opt/homebrew/Cellar/python@3.14/3.14.2_1/Frameworks/Python.framework/Versions/3.14/lib/libpython3.14.dylib",
                "/opt/homebrew/Cellar/python@3.13/3.13.12/Frameworks/Python.framework/Versions/3.13/lib/libpython3.13.dylib",
                // Homebrew Framework paths
                "/opt/homebrew/Frameworks/Python.framework/Versions/Current/lib/libpython3.dylib",
                "/usr/local/Frameworks/Python.framework/Versions/Current/lib/libpython3.dylib",
                // Generic names
                "libpython3.14.dylib", "libpython3.13.dylib", "libpython3.12.dylib",
                "libpython3.dylib"
        );

        private static final List<String> PYTHON_LIBS_LINUX = List.of(
                "libpython3.13.so", "libpython3.12.so", "libpython3.11.so", "libpython3.so"
        );

        private static final List<String> PYTHON_LIBS_WINDOWS = List.of(
                "python313.dll", "python312.dll", "python311.dll", "python3.dll"
        );

        private List<String> getPythonLibs() {
            var os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) return PYTHON_LIBS_MACOS;
            if (os.contains("win")) return PYTHON_LIBS_WINDOWS;
            return PYTHON_LIBS_LINUX;
        }

        /**
         * Verifies segment content by executing Python's {@code print()} function.
         * <p>
         * This test embeds the Python interpreter, builds a Python script dynamically, and executes
         * it to print the "hello, world" content.
         */
        @DisplayName("should verify the <segment> content through Python <print>")
        @Disabled
        @Test
        void _print_()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            assumeTrue(
                    isLibraryAvailable(getPythonLibs().toArray(String[]::new)),
                    "Python library not found - skipping test"
            );
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            final var linker = Linker.nativeLinker();
            try (var arena = Arena.ofConfined()) {
                final var python = findLibrary(arena,
                                               getPythonLibs().toArray(
                                                       String[]::new)).orElseThrow();
                final var pyInitialize = linker.downcallHandle(
                        python.find("Py_Initialize").orElseThrow(),
                        FunctionDescriptor.ofVoid()
                );
                final var pyRunSimpleString = linker.downcallHandle(
                        python.find("PyRun_SimpleString").orElseThrow(),
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
                );
                final var pyFinalize = linker.downcallHandle(
                        python.find("Py_Finalize").orElseThrow(),
                        FunctionDescriptor.ofVoid()
                );
                final var segment = arena.allocate(HelloWorld.BYTES + 1);
                final var content = readSegmentAsString(segment);
                final var script = arena.allocateFrom(
                        "print('Python says: " + content + "')",
                        StandardCharsets.UTF_8
                );
                // ---------------------------------------------------------------------------- when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                pyInitialize.invoke();
                try {
                    final var result = (int) pyRunSimpleString.invoke(script);
                    assertEquals(0, result, "PyRun_SimpleString should succeed");
                } finally {
                    pyFinalize.invoke();
                }
            }
        }
    }

    // ---------------------------------------------------------------------------- System Libraries

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} with macOS/BSD system calls.
     * <p>
     * <b>POSIX write()</b> is a low-level system call that writes data to a file descriptor.
     * It is part of the POSIX standard and available on all Unix-like systems.
     *
     * <h2>Function Used</h2>
     * <ul>
     *   <li>{@code ssize_t write(int fd, const void *buf, size_t count)} - Writes up to
     *       {@code count} bytes from buffer {@code buf} to file descriptor {@code fd}.
     *       Returns number of bytes written, or -1 on error.</li>
     * </ul>
     * <p>
     * File descriptor 1 is {@code stdout} (standard output).
     *
     * @see <a href="https://man7.org/linux/man-pages/man2/write.2.html">write(2) - Linux manual</a>
     * @see <a
     * href="https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/write.2.html">write(2)
     * - Apple</a>
     */
    @DisplayName("macOS")
    @Nested
    @EnabledOnOs(OS.MAC)
    class MacOS_Test
            extends HelloWorld__Test {

        /**
         * Verifies segment content using POSIX {@code write()} to stdout.
         * <p>
         * Writes directly to file descriptor 1 (stdout) bypassing buffered I/O.
         */
        @DisplayName("should verify the <segment> content through POSIX <write> on macOS")
        @Test
        void _write_()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            try (var arena = Arena.ofConfined()) {
                final var segment = arena.allocate(HelloWorld.BYTES);
                final var linker = Linker.nativeLinker();
                final var write = linker.downcallHandle(
                        linker.defaultLookup().find("write").orElseThrow(),
                        FunctionDescriptor.of(
                                ValueLayout.JAVA_LONG,  // ssize_t return
                                ValueLayout.JAVA_INT,   // int fd
                                ValueLayout.ADDRESS,    // const void *buf
                                ValueLayout.JAVA_LONG   // size_t count
                        )
                );
                // ---------------------------------------------------------------------------- when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                System.out.print("macOS write() says: ");
                System.out.flush();
                final var bytesWritten = (long) write.invoke(1, segment, (long) HelloWorld.BYTES);
                System.out.println();  // newline after native write
                assertEquals(HelloWorld.BYTES, bytesWritten);
            }
        }
    }

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} with Linux system calls.
     * <p>
     * Uses the same POSIX {@code write()} system call as macOS. On Linux, this is provided by glibc
     * (GNU C Library) or musl.
     *
     * @see <a href="https://man7.org/linux/man-pages/man2/write.2.html">write(2) - Linux manual</a>
     */
    @DisplayName("Linux")
    @Nested
    @EnabledOnOs(OS.LINUX)
    class Linux_Test
            extends HelloWorld__Test {

        /**
         * Verifies segment content using POSIX {@code write()} to stdout.
         */
        @DisplayName("should verify the <segment> content through POSIX <write> on Linux")
        @Test
        void _write_()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            try (var arena = Arena.ofConfined()) {
                final var segment = arena.allocate(HelloWorld.BYTES);
                final var linker = Linker.nativeLinker();
                final var write = linker.downcallHandle(
                        linker.defaultLookup().find("write").orElseThrow(),
                        FunctionDescriptor.of(
                                ValueLayout.JAVA_LONG,
                                ValueLayout.JAVA_INT,
                                ValueLayout.ADDRESS,
                                ValueLayout.JAVA_LONG
                        )
                );
                // ---------------------------------------------------------------------------- when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                System.out.print("Linux write() says: ");
                System.out.flush();
                final var bytesWritten = (long) write.invoke(1, segment, (long) HelloWorld.BYTES);
                System.out.println();
                assertEquals(HelloWorld.BYTES, bytesWritten);
            }
        }
    }

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} with Windows API (kernel32.dll).
     * <p>
     * <b>Windows API</b> provides system services through DLLs. {@code kernel32.dll}
     * contains core OS functions including console I/O.
     *
     * <h2>Functions Used</h2>
     * <ul>
     *   <li>{@code HANDLE GetStdHandle(DWORD nStdHandle)} - Returns a handle for the
     *       specified standard device. {@code STD_OUTPUT_HANDLE} (-11) is stdout.</li>
     *   <li>{@code BOOL WriteConsoleA(HANDLE hConsoleOutput, const VOID *lpBuffer,
     *       DWORD nNumberOfCharsToWrite, LPDWORD lpNumberOfCharsWritten, LPVOID lpReserved)}
     *       - Writes ANSI characters to the console. Returns nonzero on success.</li>
     * </ul>
     *
     * @see <a
     * href="https://learn.microsoft.com/en-us/windows/console/getstdhandle">GetStdHandle</a>
     * @see <a
     * href="https://learn.microsoft.com/en-us/windows/console/writeconsolea">WriteConsoleA</a>
     */
    @DisplayName("Windows")
    @Nested
    @EnabledOnOs(OS.WINDOWS)
    class Windows_Test
            extends HelloWorld__Test {

        /**
         * Verifies segment content using Windows {@code WriteConsoleA()}.
         * <p>
         * Gets the stdout handle via {@code GetStdHandle(STD_OUTPUT_HANDLE)} and writes the segment
         * content to the console.
         */
        @DisplayName("should verify the <segment> content through Windows <WriteConsoleA>")
        @Test
        void _WriteConsoleA_()
                throws Throwable {
            // ------------------------------------------------------------------------------- given
            assumeTrue(
                    isLibraryAvailable("kernel32", "kernel32.dll"),
                    "kernel32.dll not found"
            );
            final var service = service();
            stubSetArrayWillCopyHelloWorldBytes(service);
            final var linker = Linker.nativeLinker();
            try (var arena = Arena.ofConfined()) {
                final var kernel32 = findLibrary(arena, "kernel32", "kernel32.dll").orElseThrow();
                final var getStdHandle = linker.downcallHandle(
                        kernel32.find("GetStdHandle").orElseThrow(),
                        FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
                );
                final var writeConsoleA = linker.downcallHandle(
                        kernel32.find("WriteConsoleA").orElseThrow(),
                        FunctionDescriptor.of(
                                ValueLayout.JAVA_INT,
                                ValueLayout.ADDRESS,
                                ValueLayout.ADDRESS,
                                ValueLayout.JAVA_INT,
                                ValueLayout.ADDRESS,
                                ValueLayout.ADDRESS
                        )
                );
                final var segment = arena.allocate(HelloWorld.BYTES);
                // ---------------------------------------------------------------------------- when
                service.copy(segment);
                // ---------------------------------------------------------------------------- then
                final var stdout = (MemorySegment) getStdHandle.invoke(-11);
                final var bytesWritten = arena.allocate(ValueLayout.JAVA_INT);
                System.out.print("Windows WriteConsoleA says: ");
                System.out.flush();
                final var result = (int) writeConsoleA.invoke(
                        stdout, segment, HelloWorld.BYTES, bytesWritten, MemorySegment.NULL
                );
                System.out.println();
                assertNotEquals(0, result, "WriteConsoleA should succeed");
            }
        }
    }
}
