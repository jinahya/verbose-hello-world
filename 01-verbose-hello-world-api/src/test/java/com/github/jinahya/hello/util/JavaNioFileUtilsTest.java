package com.github.jinahya.hello.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

class JavaNioFileUtilsTest {

    @DisplayName("hexl(path)")
    @Nested
    class HexlTest {

        @Test
        void __(@TempDir final Path tempDir) throws IOException {
            final var path = Files.createTempFile(tempDir, null, null);
            JavaNioFileUtils.fillRandom(path, ThreadLocalRandom.current().nextInt(1024, 2048), 0L);
            JavaNioFileUtils.hexl(path, StandardOpenOption.READ);
        }
    }
}
