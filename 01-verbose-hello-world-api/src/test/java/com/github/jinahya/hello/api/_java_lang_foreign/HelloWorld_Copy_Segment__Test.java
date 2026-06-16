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
import com.github.jinahya.hello.api.annotations.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.io.*;

import java.lang.foreign.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._java_lang_foreign._Arena_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Tests {@link HelloWorld#copy(MemorySegment)} with various native libraries via FFM API. Tests are
 * conditionally skipped based on OS and library availability.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_NotForPublishing
@DisplayName("copy(segment) via FFM")
class HelloWorld_Copy_Segment__Test extends HelloWorld__Test {

    @BeforeEach
    void __stubService() {
        set_array_sets_hello_world_bytes(service());
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
    class Libc_Test {

        /**
         * Verifies segment content using C {@code puts()}.
         * <p>
         * {@code int puts(const char *s)} writes the string {@code s} and a trailing newline to
         * {@code stdout}. Requires null-terminated string.
         */
        @DisplayName("puts")
        @Test
        void _puts__() throws Throwable {
            // ------------------------------------------------------------------------------- given
            try (var arena = Arena.ofConfined()) {
                // +1 for null terminator (puts requires null-terminated string)
                final var segment = arena.allocate(HelloWorld.BYTES + 1);
                // ----------------------------------------------------------------------------when
                service().copy(segment);
                // ---------------------------------------------------------------------------- then
                final var content = readSegmentAsString(segment, HelloWorld.BYTES);
                assertEquals(HELLO_WORLD_STRING, content);
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
        @DisplayName("strlen")
        @Test
        void _strlen__() throws Throwable {
            // ------------------------------------------------------------------------------- given
            try (var arena = Arena.ofConfined()) {
                final var segment = arena.allocate(HelloWorld.BYTES + 1);
                // ---------------------------------------------------------------------------- when
                service().copy(segment);
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
        @DisplayName("memcmp")
        @Test
        void _memcmp__() throws Throwable {
            // ------------------------------------------------------------------------------- given
            try (var arena = Arena.ofConfined()) {
                final var segment = arena.allocate(HelloWorld.BYTES);
                // ---------------------------------------------------------------------------- when
                service().copy(segment);
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
    class MacOS_Test {

        /**
         * Verifies segment content using POSIX {@code write()} to stdout.
         * <p>
         * Writes directly to file descriptor 1 (stdout) bypassing buffered I/O.
         */
        @DisplayName("write")
        @Test
        void _write_() throws Throwable {
            // ------------------------------------------------------------------------------- given
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
                service().copy(segment);
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
    class Linux_Test {

        /**
         * Verifies segment content using POSIX {@code write()} to stdout.
         */
        @DisplayName("write")
        @Test
        void _write_() throws Throwable {
            // ------------------------------------------------------------------------------- given
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
                service().copy(segment);
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
    class Windows_Test {

        /**
         * Verifies segment content using Windows {@code WriteConsoleA()}.
         * <p>
         * Gets the stdout handle via {@code GetStdHandle(STD_OUTPUT_HANDLE)} and writes the segment
         * content to the console.
         */
        @DisplayName("WriteConsoleA")
        @Test
        void _WriteConsoleA_() throws Throwable {
            // ------------------------------------------------------------------------------- given
            assumeTrue(
                    isLibraryAvailable("kernel32", "kernel32.dll"),
                    "kernel32.dll not found"
            );
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
                service().copy(segment);
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

    // --------------------------------------------------------------------------------------- zlib

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} with zlib's {@code crc32} function — a
     * cross-platform native checksum routine that consumes the segment as a {@code const Bytef *}
     * and returns the CRC-32 of its contents.
     *
     * <h2>Function Used</h2>
     * <ul>
     *   <li>{@code unsigned long crc32(unsigned long crc, const Bytef *buf, uInt len)} —
     *       updates the running CRC-32 ({@code crc}) by processing {@code len} bytes from
     *       {@code buf}. Pass {@code 0} to start a fresh checksum.</li>
     * </ul>
     * <p>
     * The expected value is cross-checked against {@link CRC32}.
     *
     * @see <a href="https://zlib.net/manual.html#Checksum">zlib manual — Checksum
     * functions</a>
     */
    @DisplayName("zlib")
    @Nested
    class Zlib_Test {

        private static final String[] ZLIB_LIBS = {
                "libz.so.1", "libz.so",
                "libz.dylib",
                "zlib1.dll", "zlib.dll"
        };

        /**
         * Verifies the segment via zlib {@code crc32()}; the result must equal the CRC-32 of the
         * hello-world bytes as computed by {@link CRC32}.
         */
        @DisplayName("crc32")
        @Test
        void _crc32__() throws Throwable {
            assumeTrue(isLibraryAvailable(ZLIB_LIBS), "zlib not found");
            try (var arena = Arena.ofConfined()) {
                final var zlib = findLibrary(arena, ZLIB_LIBS).orElseThrow();
                final var linker = Linker.nativeLinker();
                // C 'unsigned long' is 64-bit on POSIX LP64, 32-bit on Windows LLP64
                final var uLong = linker.canonicalLayouts().get("long");
                final var crc32 = linker.downcallHandle(
                        zlib.find("crc32").orElseThrow(),
                        FunctionDescriptor.of(
                                uLong,                  // unsigned long  (return)
                                uLong,                  // unsigned long  crc
                                ValueLayout.ADDRESS,    // const Bytef *  buf
                                ValueLayout.JAVA_INT    // uInt           len
                        )
                );
                final var segment = arena.allocate(HelloWorld.BYTES);
                // ---------------------------------------------------------------------------- when
                service().copy(segment);
                // ---------------------------------------------------------------------------- then
                final long actual;
                if (uLong.byteSize() == Long.BYTES) {
                    actual = (long) crc32.invoke(0L, segment, HelloWorld.BYTES);
                } else {
                    actual = Integer.toUnsignedLong(
                            (int) crc32.invoke(0, segment, HelloWorld.BYTES));
                }
                final var expected = new CRC32();
                expected.update(hello_world_byte_array());
                assertEquals(expected.getValue(), actual,
                             "zlib crc32 should equal java.util.zip.CRC32");
            }
        }
    }

    // ---------------------------------------------------- java.lang.foreign.MemorySegment#ofArray

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} against an on-heap segment wrapping a
     * {@code byte[]} via {@link MemorySegment#ofArray(byte[])}. No arena, no off-heap allocation —
     * the "first touch" example.
     */
    @DisplayName("ofArray (heap byte[])")
    @Nested
    class OfArray_Test {

        /**
         * Verifies that {@code copy} populates the heap array backing the segment.
         */
        @DisplayName("MemorySegment.ofArray(new byte[BYTES])")
        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[HelloWorld.BYTES];
            final var segment = MemorySegment.ofArray(array);
            // -------------------------------------------------------------------------------- when
            service().copy(segment);
            // -------------------------------------------------------------------------------- then
            assertEquals(HELLO_WORLD_STRING, new String(array, StandardCharsets.US_ASCII));
        }
    }

    // --------------------------------------------------- java.lang.foreign.MemorySegment#ofBuffer

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} against a segment wrapping a
     * {@link java.nio.ByteBuffer} via {@link MemorySegment#ofBuffer(java.nio.Buffer)} — the FFM ↔
     * NIO bridge — for both heap and direct buffers.
     */
    @DisplayName("ofBuffer (NIO bridge)")
    @Nested
    class OfBuffer_Test {

        /**
         * Verifies that {@code copy} populates a segment wrapping a
         * {@linkplain ByteBuffer#allocate(int) heap} buffer.
         */
        @DisplayName("ofBuffer(ByteBuffer.allocate(BYTES))")
        @Test
        void _heap__() {
            // ------------------------------------------------------------------------------- given
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            final var segment = MemorySegment.ofBuffer(buffer);
            // -------------------------------------------------------------------------------- when
            service().copy(segment);
            // -------------------------------------------------------------------------------- then
            assertEquals(HELLO_WORLD_STRING,
                         StandardCharsets.US_ASCII.decode(buffer).toString());
        }

        /**
         * Verifies that {@code copy} populates a segment wrapping a
         * {@linkplain ByteBuffer#allocateDirect(int) direct} buffer.
         */
        @DisplayName("ofBuffer(ByteBuffer.allocateDirect(BYTES))")
        @Test
        void _direct__() {
            // ------------------------------------------------------------------------------- given
            final var buffer = ByteBuffer.allocateDirect(HelloWorld.BYTES);
            final var segment = MemorySegment.ofBuffer(buffer);
            // -------------------------------------------------------------------------------- when
            service().copy(segment);
            // -------------------------------------------------------------------------------- then
            assertEquals(HELLO_WORLD_STRING,
                         StandardCharsets.US_ASCII.decode(buffer).toString());
        }
    }

    // ---------------------------------------------------------- java.nio.channels.FileChannel#map

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} against a segment backed by a memory-mapped file
     * via {@link FileChannel#map(FileChannel.MapMode, long, long, Arena)} — the FFM-meets-I/O
     * pathway. Strictly portable.
     */
    @DisplayName("FileChannel.map (memory-mapped file)")
    @Nested
    class Mapped_Test {

        /**
         * Verifies that {@code copy} writes to a memory-mapped region and the bytes land on disk.
         *
         * @param tempDir the per-test temp dir holding the mapped file.
         */
        @DisplayName("channel.map(READ_WRITE, 0, BYTES, arena)")
        @Test
        void __(@TempDir final Path tempDir) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var path = tempDir.resolve("hello.bin");
            try (var channel = FileChannel.open(path, StandardOpenOption.CREATE,
                                                StandardOpenOption.READ,
                                                StandardOpenOption.WRITE);
                 var arena = Arena.ofShared()) {
                final var segment = channel.map(FileChannel.MapMode.READ_WRITE,
                                                0L, HelloWorld.BYTES, arena);
                // ---------------------------------------------------------------------------- when
                service().copy(segment);
                segment.force();
            }
            // ----------------------------------------------------------------------------------then
            assertEquals(HELLO_WORLD_STRING,
                         Files.readString(path, StandardCharsets.US_ASCII));
        }
    }

    // ---------------------------------------------------- java.lang.foreign.MemorySegment#asSlice

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} via {@link MemorySegment#asSlice(long)} — the
     * supported way to write at a non-zero offset, per the method's {@code @apiNote}.
     */
    @DisplayName("asSlice (write at offset)")
    @Nested
    class AsSlice_Test {

        /**
         * Verifies that {@code copy(segment.asSlice(offset))} writes only into
         * {@code [offset, offset+BYTES)} and leaves {@code [0, offset)} untouched.
         */
        @DisplayName("copy(segment.asSlice(5))")
        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var offset = 5;
            acceptConfinedArena(arena -> {
                final var segment = arena.allocate(offset + HelloWorld.BYTES);
                // ---------------------------------------------------------------------------- when
                service().copy(segment.asSlice(offset));
                // ---------------------------------------------------------------------------- then
                for (var i = 0L; i < offset; i++) {
                    assertEquals((byte) 0, segment.get(ValueLayout.JAVA_BYTE, i),
                                 "byte at " + i + " must stay zero");
                }
                assertEquals(HELLO_WORLD_STRING,
                             readSegmentAsString(segment.asSlice(offset), HelloWorld.BYTES));
            });
        }
    }

    // ---------------------------------------------------------------- java.lang.foreign.Arena#of*

    /**
     * Tests {@link HelloWorld#copy(MemorySegment)} against off-heap segments allocated by each
     * {@link Arena} lifetime variant — confined, shared, automatic, and global.
     */
    @DisplayName("Arena lifetime variants")
    @Nested
    class Arenas_Test {

        /**
         * Verifies {@code copy(segment)} against a segment allocated by {@link Arena#ofConfined()}
         * — single-thread, explicit-close.
         */
        @DisplayName("Arena.ofConfined()")
        @Test
        void _confined__() {
            acceptConfinedArena(arena -> {
                final var segment = arena.allocate(HelloWorld.BYTES);
                // -------------------------------------------------------------------------- when
                service().copy(segment);
                // -------------------------------------------------------------------------- then
                assertEquals(HELLO_WORLD_STRING, readSegmentAsString(segment, HelloWorld.BYTES));
            });
        }

        /**
         * Verifies {@code copy(segment)} against a segment allocated by {@link Arena#ofShared()} —
         * multi-thread, explicit-close.
         */
        @DisplayName("Arena.ofShared()")
        @Test
        void _shared__() {
            // ------------------------------------------------------------------------------- given
            try (var arena = Arena.ofShared()) {
                final var segment = arena.allocate(HelloWorld.BYTES);
                // ---------------------------------------------------------------------------- when
                service().copy(segment);
                // ---------------------------------------------------------------------------- then
                assertEquals(HELLO_WORLD_STRING, readSegmentAsString(segment, HelloWorld.BYTES));
            }
        }

        /**
         * Verifies {@code copy(segment)} against a segment allocated by {@link Arena#ofAuto()} —
         * GC-managed, no explicit close.
         */
        @DisplayName("Arena.ofAuto()")
        @SuppressWarnings({"resource", "java:S2095"}) // Arena.ofAuto() is GC-managed; no close
        @Test
        void _auto__() {
            // ------------------------------------------------------------------------------- given
            final var arena = Arena.ofAuto();
            final var segment = arena.allocate(HelloWorld.BYTES);
            // -------------------------------------------------------------------------------- when
            service().copy(segment);
            // -------------------------------------------------------------------------------- then
            assertEquals(HELLO_WORLD_STRING, readSegmentAsString(segment, HelloWorld.BYTES));
        }

        /**
         * Verifies {@code copy(segment)} against a segment allocated by {@link Arena#global()} —
         * never-released global lifetime.
         */
        @DisplayName("Arena.global()")
        @SuppressWarnings({"resource", "java:S2095"}) // Arena.global() never closes
        @Test
        void _global__() {
            // ------------------------------------------------------------------------------- given
            final var arena = Arena.global();
            final var segment = arena.allocate(HelloWorld.BYTES);
            // -------------------------------------------------------------------------------- when
            service().copy(segment);
            // -------------------------------------------------------------------------------- then
            assertEquals(HELLO_WORLD_STRING, readSegmentAsString(segment, HelloWorld.BYTES));
        }
    }
}
