package com.github.jinahya.hello.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

class JavaIoUtilsTest {

    @DisplayName("hexl(file)")
    @Nested
    class HexlTest {

        @Test
        void __(@TempDir final File tempDir) throws IOException {
            final var file = File.createTempFile("tmp", "tmp", tempDir);
            JavaIoUtils.fillRandom(file, ThreadLocalRandom.current().nextInt(1024, 2048), 0L);
            JavaIoUtils.hexl(file);
        }
    }
}
