package com.github.jinahya.hello.api._java_io;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class exercising {@link HelloWorld#append(File) append(file)} against a real {@link File}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(file)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Append_File__Test extends HelloWorld__Test {

    /**
     * The {@link TempDir} shared by tests in this class.
     */
    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.append(file)} to append the {@code hello-world-bytes} to {@code file}.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __stubService() throws IOException {
        append_file_appends_hello_world(service());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@link HelloWorld#append(File) append(file)} throws a
     * {@link FileNotFoundException} when the {@code file} is a directory.
     */
    @DisplayName("should throw a <FileNotFoundException> when the <file> is a <directory>")
    @Test
    void __Directory() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var directory = new File(tempDir, Long.toString(System.nanoTime()));
        assert directory.mkdir();
        assert directory.exists();
        assert !directory.isFile();
        assert directory.isDirectory();
        final var length = directory.length(); // unspecified
        // ----------------------------------------------------------------------------- when / then
        assertThrows(FileNotFoundException.class, () -> service.append(directory));
    }

    /**
     * Verifies that {@link HelloWorld#append(File) append(file)} increases the {@code file}'s
     * length by {@value HelloWorld#BYTES} bytes when the {@code file} exists.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should increase the <file>'s length by <12> when the <file> exists")
    @Test
    void __Existing() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = File.createTempFile("tmp", null, tempDir);
        assert file.exists();
        assert file.isFile();
        assert !file.isDirectory();
        HelloWorld__TestUtils.writeSome(file);
        final var length = file.length();
        // ------------------------------------------------------------------------------------ when
        service.append(file);
        // ------------------------------------------------------------------------------------ then
        assertEquals(length + HelloWorld.BYTES, file.length());
    }

    /**
     * Verifies that {@link HelloWorld#append(File) append(file)} creates the {@code file} with
     * {@value HelloWorld#BYTES} bytes when the {@code file} does not exist.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should create the <file> with <12> bytes when the <file> does not exist")
    @Test
    void __NotExisting() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = new File(tempDir, Long.toString(System.nanoTime()));
        assert !file.exists();
        assert !file.isFile();
        assert !file.isDirectory();
        final var length = file.length();
        assert length == 0;
        // ------------------------------------------------------------------------------------ when
        service.append(file);
        // ------------------------------------------------------------------------------------ then
        assertTrue(file.isFile());
        assertEquals(HelloWorld.BYTES, file.length());
    }

    /**
     * Verifies that {@link HelloWorld#append(File) append(file)} produces a file of
     * {@value HelloWorld#BYTES} bytes regardless of whether the file existed beforehand.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should increase the <file>'s length by <12> when the <file> exists")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = File.createTempFile("tmp", null, tempDir);
        assert file.exists();
        assert file.isFile();
        assert !file.isDirectory();
        if (ThreadLocalRandom.current().nextBoolean()) {
            final var deleted = file.delete();
            assert deleted;
        }
        // ------------------------------------------------------------------------------------ when
        service.append(file);
        // ------------------------------------------------------------------------------------ then
        assertEquals(HelloWorld.BYTES, file.length());
    }
}
