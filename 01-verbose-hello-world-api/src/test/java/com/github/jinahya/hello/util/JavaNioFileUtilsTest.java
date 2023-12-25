package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import com.github.jinahya.hello.util.java.nio.JavaNioFileUtils;
import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

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
        void createTempFileInAndWriteSome_IsFile_(@TempDir final Path dir) throws IOException {
            try (var mock = Mockito.mockStatic(JavaNioUtils.class, Mockito.CALLS_REAL_METHODS)) {
                final var path = JavaNioUtils.createTempFileInAndWriteSome(dir);
                Assertions.assertNotNull(path);
                Assertions.assertTrue(Files.isRegularFile(path));
                mock.verify(
                        () -> {
                            JavaNioUtils.writeSome(ArgumentMatchers.argThat(p -> {
                                return (p != null && Files.isRegularFile(p)) &&
                                       (p.getParent() != null && p.getParent().equals(dir));
                            }));
                        },
                        Mockito.times(1)
                );
            }
        }
    }
}
