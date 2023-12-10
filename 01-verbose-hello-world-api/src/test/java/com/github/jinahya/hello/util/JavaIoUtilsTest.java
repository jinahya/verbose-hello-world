package com.github.jinahya.hello.util;

import com.github.jinahya.hello.util.java.io._JavaIoUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

class JavaIoUtilsTest {

    @DisplayName("hexl(file)")
    @Nested
    class HexlTest {

        @Test
        void __(@TempDir final File tempDir)
                throws IOException {
            final var file = File.createTempFile("tmp", "tmp", tempDir);
            _JavaIoUtils.fillRandom(file, ThreadLocalRandom.current().nextInt(1024, 2048), 0L);
            _JavaIoUtils.hexl(file);
        }
    }

    @DisplayName("createTempFileInAndWriteSome")
    @Nested
    class CreateTempFileInAndWriteSomeTest {

        @DisplayName("(dir)isFile")
        @Test
        void createTempFileInAndWriteSome_IsFile_(@TempDir final File dir)
                throws IOException {
            try (var mock = Mockito.mockStatic(_JavaIoUtils.class, Mockito.CALLS_REAL_METHODS)) {
                final var file = _JavaIoUtils.createTempFileInAndWriteSome(dir);
                assertThat(file).isNotNull().isFile();
                mock.verify(
                        () -> {
                            _JavaIoUtils.writeSome(argThat(f -> {
                                return (f != null && f.isFile()) &&
                                       (f.getParent() != null && f.getParentFile().equals(dir));
                            }));
                        },
                        times(1)
                );
            }
        }
    }
}
