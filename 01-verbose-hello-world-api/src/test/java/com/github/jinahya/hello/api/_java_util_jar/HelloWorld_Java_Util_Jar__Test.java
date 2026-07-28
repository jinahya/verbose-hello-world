package com.github.jinahya.hello.api._java_util_jar;

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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.util.jar.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link HelloWorld#write(OutputStream) write(stream)} method with real
 * {@link JarOutputStream} / {@link JarFile} from {@code java.util.jar}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("java.util.jar")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Jar__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        write_stream_writes_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("JarOutputStream")
    @Nested
    class JarOutputStream_Test {

        /**
         * Verifies that the {@link HelloWorld#write(OutputStream) write(stream)} method writes a
         * {@code "hello, world"} entry into an in-memory {@link JarOutputStream}, and that the
         * bytes can be read back through {@link JarInputStream}.
         */
        @DisplayName("in-memory")
        @Test
        void __() throws IOException {
            final var name = "hello-world.txt";
            try (var baos = new ByteArrayOutputStream();
                 var jos = new JarOutputStream(baos)) {
                jos.putNextEntry(new JarEntry(name));
                service().write(jos);
                jos.closeEntry();
                jos.flush();
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     var jis = new JarInputStream(bais)) {
                    final var entry = jis.getNextJarEntry();
                    assert entry != null;
                    assert entry.getName().equals(name);
                    assertArrayEquals(hello_world_byte_array(), jis.readAllBytes());
                }
            }
        }

        /**
         * Verifies that the {@link HelloWorld#write(OutputStream) write(stream)} method writes a
         * {@code "hello, world"} entry into a file-backed {@link JarOutputStream}, and that the
         * bytes can be read back through {@link JarFile}.
         */
        @DisplayName("file-backed")
        @Test
        void __File() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var tempFile = File.createTempFile("tmp", null, tempDir);
            final var entryName = "hello-world.bin";
            // -------------------------------------------------------------------------------- when
            try (var fos = new FileOutputStream(tempFile);
                 var jos = new JarOutputStream(fos)) {
                jos.putNextEntry(new JarEntry(entryName));
                service().write(jos);
                jos.closeEntry();
                jos.flush();
            }
            // -------------------------------------------------------------------------------- then
            try (var jarFile = new JarFile(tempFile)) {
                final var entry = jarFile.getJarEntry(entryName);
                assert entry != null;
                try (var in = jarFile.getInputStream(entry)) {
                    assertArrayEquals(hello_world_byte_array(), in.readAllBytes());
                }
            }
        }
    }

    @DisplayName("JarFile")
    @Nested
    class JarFile_Test {

        /**
         * Verifies that a {@code "hello, world"} entry written via
         * {@link HelloWorld#write(OutputStream) write(stream)} can be read back through a
         * {@link JarFile}.
         */
        @DisplayName("happy path")
        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var file = File.createTempFile("tmp", null, tempDir);
            final var name = "hello-world.bin";
            // -------------------------------------------------------------------------------- when
            try (var fos = new FileOutputStream(file);
                 var jos = new JarOutputStream(fos)) {
                jos.putNextEntry(new JarEntry(name));
                service().write(jos);
                jos.closeEntry();
                jos.flush();
            }
            // -------------------------------------------------------------------------------- then
            try (var jf = new JarFile(file)) {
                final var entry = jf.getJarEntry(name);
                assert entry != null;
                assert entry.getName().equals(name);
                try (var in = jf.getInputStream(entry)) {
                    assertArrayEquals(hello_world_byte_array(), in.readAllBytes());
                }
            }
        }
    }
}
