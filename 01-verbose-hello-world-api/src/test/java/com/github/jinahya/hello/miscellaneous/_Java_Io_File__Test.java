package com.github.jinahya.hello.miscellaneous;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link java.io.File} behavior with {@link FileOutputStream}.
 */
@DisplayName("java.io.File")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class _Java_Io_File__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that opening a {@link FileOutputStream} on a {@code directory} throws a
     * {@link FileNotFoundException}.
     */
    @DisplayName("opening a <FileOutputStream> on a <directory> throws <FileNotFoundException>")
    @Test
    void __Directory() {
        // ----------------------------------------------------------------------------------- given
        final var directory = new File(tempDir, Long.toString(System.nanoTime()));
        assert directory.mkdir();
        assert directory.exists();
        assert !directory.isFile();
        assert directory.isDirectory();
        final var length = directory.length(); // unspecified
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                FileNotFoundException.class,
                () -> {
                    try (var _ = new FileOutputStream(directory, true)) {
                    }
                }
        );
    }

    /**
     * Verifies that appending {@code N} bytes to an existing {@code file} increases its length by
     * {@code N}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("appending <N> bytes to an existing <file> increases its length by <N>")
    @Test
    void __Existing() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var file = File.createTempFile("tmp", null, tempDir);
        assert file.exists();
        assert file.isFile();
        assert !file.isDirectory();
        try (var stream = new FileOutputStream(file)) {
            stream.write(new byte[ThreadLocalRandom.current().nextInt(128)]);
        }
        final var length = file.length();
        final var bytes = new byte[ThreadLocalRandom.current().nextInt(1, 128)];
        // ------------------------------------------------------------------------------------ when
        try (var stream = new FileOutputStream(file, true)) {
            stream.write(bytes);
        }
        // ------------------------------------------------------------------------------------ then
        assertEquals(length + bytes.length, file.length());
    }

    /**
     * Verifies that writing {@code N} bytes to a non-existing {@code file} creates it with length
     * {@code N}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("writing <N> bytes to a non-existing <file> creates it with length <N>")
    @Test
    void __NotExisting() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var file = new File(tempDir, Long.toString(System.nanoTime()));
        assert !file.exists();
        assert !file.isFile();
        assert !file.isDirectory();
        assert file.length() == 0;
        final var bytes = new byte[ThreadLocalRandom.current().nextInt(1, 128)];
        // ------------------------------------------------------------------------------------ when
        try (var stream = new FileOutputStream(file)) {
            stream.write(bytes);
        }
        // ------------------------------------------------------------------------------------ then
        assertTrue(file.isFile());
        assertEquals(bytes.length, file.length());
    }
}
