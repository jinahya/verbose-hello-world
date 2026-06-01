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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.util.jar.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

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

    @DisplayName("JarOutputStream")
    @Nested
    class JarOutputStream_Test {

        @DisplayName("should write a <hello, world> entry into an in-memory <JarOutputStream>")
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

        @DisplayName("should write a <hello, world> entry into a file-backed <JarOutputStream>")
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

        @DisplayName("should read back a <hello, world> entry through <JarFile>")
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
