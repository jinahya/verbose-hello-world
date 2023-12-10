package com.github.jinahya.hello.util;

import com.github.jinahya.hello.util.java.nio.JavaNioFileUtils;
import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

class JavaNioFileUtilsTest {

    @DisplayName("hexl(path)")
    @Nested
    class HexlTest {

        @Test
        void __(@TempDir final Path tempDir)
                throws IOException {
            final var path = Files.createTempFile(tempDir, null, null);
            JavaNioFileUtils.fillRandom(path, ThreadLocalRandom.current().nextInt(1024, 2048), 0L);
            JavaNioFileUtils.hexl(path, StandardOpenOption.READ);
        }
    }

    @DisplayName("createTempFileInAndWriteSome")
    @Nested
    class CreateTempFileInAndWriteSomeTest {

        @DisplayName("(dir)isFile")
        @Test
        void createTempFileInAndWriteSome_IsFile_(@TempDir final Path dir)
                throws IOException {
            try (var mock = Mockito.mockStatic(JavaNioUtils.class, Mockito.CALLS_REAL_METHODS)) {
                final var path = JavaNioUtils.createTempFileInAndWriteSome(dir);
                assertThat(path).isNotNull().isRegularFile();
                mock.verify(
                        () -> {
                            JavaNioUtils.writeSome(argThat(p -> {
                                return (p != null && Files.isRegularFile(p)) &&
                                       (p.getParent() != null && p.getParent().equals(dir));
                            }));
                        },
                        times(1)
                );
            }
        }
    }
}
