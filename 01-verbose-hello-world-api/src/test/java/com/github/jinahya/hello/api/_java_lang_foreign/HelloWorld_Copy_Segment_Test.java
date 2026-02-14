package com.github.jinahya.hello.api._java_lang_foreign;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Copy_Segment_Test extends HelloWorldTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        stub_set_array_will_return_the_array();
        try (var arena = Arena.ofConfined()) {
            final var segment = arena.allocate(HelloWorld.BYTES);
            try (var mockedStatic = Mockito.mockStatic(MemorySegment.class,
                                                       Mockito.CALLS_REAL_METHODS)) {
                // ---------------------------------------------------------------------------- when
                final var result = service.copy(segment);
                // ---------------------------------------------------------------------------- then
                Assertions.assertSame(segment, result);
                final var array = verify_set_array12_invoked_once();
                mockedStatic.verify(() -> MemorySegment.copy(
                        Mockito.same(array),
                        Mockito.eq(0),
                        Mockito.same(segment),
                        Mockito.eq(ValueLayout.JAVA_BYTE),
                        Mockito.eq(0L),
                        Mockito.eq(array.length)
                ));
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    static void compileNative(final Path source, final Path target) throws Exception {
        final boolean windows = System.getProperty("os.name").startsWith("Win");
        // List of common compilers in order of preference
        final var compilers = windows
                              ? new String[] {"gcc", "clang", "cl"}
                              : new String[] {"cc", "gcc", "clang"};
        String compiler = null;
        for (final var c : compilers) {
            try {
                final var check = new ProcessBuilder(
                        windows
                        ? new String[] {"where", c}
                        : new String[] {"which", c}
                ).start();
                if (check.waitFor() == 0) {
                    compiler = c;
                    break;
                }
            } catch (final IOException ioe) {
                // ignored
            }
        }
        log.debug("detected compiler: {}", compiler);
        Assumptions.assumeFalse(compiler == null,
                                "No C compiler found (tried gcc, clang, cc, msvc)");
        ProcessBuilder pb;
        if (compiler.equals("cl")) {
            // MSVC syntax: cl source.c /Fe:target.exe
            pb = new ProcessBuilder(compiler, source.toString(), "/Fe:" + target.toString());
        } else {
            // GCC/Clang syntax: gcc source.c -o target
            pb = new ProcessBuilder(compiler, source.toString(), "-o", target.toString());
        }
        final var exitCode = pb.inheritIO().start().waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Compilation failed with exit code " + exitCode);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void testJavaToCBridge(@TempDir final Path tempDir) throws Exception {
        final var windows = System.getProperty("os.name").startsWith("Win");
        final var sourcePath = Paths.get("src", "test", "c", "reader.c");
        final var targetPath = tempDir.resolve(windows ? "reader.exe" : "reader");

        // ------------------------------------------------------------------------- compile program
        compileNative(sourcePath, targetPath);

        // ----------------------------------------------------------------------------- shared data
        final var dataPath = tempDir.resolve("data.bin");
        try (final var channel = FileChannel.open(dataPath, StandardOpenOption.CREATE,
                                                  StandardOpenOption.READ,
                                                  StandardOpenOption.WRITE);
             final var arena = Arena.ofShared()) {
            // ---------------------------------------------------------------- write hello, world\0
            final var segment = channel.map(FileChannel.MapMode.READ_WRITE, 0, 13, arena);
            HelloWorldTestUtils.stub_set_array_will_set_actual_hello_world_bytes(service());
            service().copy(segment);
            segment.set(ValueLayout.JAVA_BYTE, 12, (byte) 0);
            log.debug("bytes written to the file");
            // ----------------------------------------------------------------- read hello, world\0
            final var process = new ProcessBuilder(targetPath.toString(),
                                                   dataPath.toAbsolutePath().toString())
                    .inheritIO()
                    .start();
            Assertions.assertEquals(0, process.waitFor());
        }
    }
}
