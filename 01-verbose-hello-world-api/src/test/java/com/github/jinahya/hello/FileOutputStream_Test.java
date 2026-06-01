package com.github.jinahya.hello;

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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.io.*;
import org.mockito.junit.jupiter.*;

import java.io.*;

@DisplayName("FileOutputStream")
@ExtendWith({MockitoExtension.class})
@Slf4j
class FileOutputStream_Test {

    @DisplayName("should create an empty file when target does not exist")
    @Test
    void __NotExist(final @TempDir File dir) throws IOException {
        final var file = new File(dir, "test.txt");
        Assertions.assertFalse(file.exists());
        new FileOutputStream(file).close();
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(0L, file.length());
    }

    @DisplayName("should create an empty file when target does not exist and <append> is <true>")
    @Test
    void __NotExistAppend(final @TempDir File dir) throws IOException {
        final var file = new File(dir, "test.txt");
        Assertions.assertFalse(file.exists());
        new FileOutputStream(file, true).close();
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(0L, file.length());
    }

    @DisplayName("should truncate existing file when <append> is <false>")
    @Test
    void __ExistNotAppend(final @TempDir File dir) throws IOException {
        final var file = new File(dir, "test.txt");
        try (var stream = new FileOutputStream(file)) {
            stream.write(0);
            stream.flush();
        }
        Assertions.assertTrue(file.exists());
        new FileOutputStream(file).close();
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(0L, file.length());
    }
}
