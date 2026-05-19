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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Jar__Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.write_stream_will_write_actual_hello_world_bytes(service());
    }

    @Nested
    class JarOutputStream_Test {

        @Test
        void __JarInputStream() throws IOException {
            // ------------------------------------------------------------------------------- given
            // -------------------------------------------------------------------------------- when
            try (var baos = new ByteArrayOutputStream();
                 var jos = new JarOutputStream(baos)) {
                jos.putNextEntry(new JarEntry("hello-world.txt"));
                service().write(jos);
                jos.closeEntry();
                jos.flush();
                // ---------------------------------------------------------------------------- then
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     var jis = new JarInputStream(bais)) {
                    final var entry = jis.getNextJarEntry();
                    assert entry != null;
                    Assertions.assertArrayEquals(
                            HelloWorldTestUtils.hello_world_byte_array(),
                            jis.readAllBytes()
                    );
                }
            }
        }

        @Test
        void __JarFile() throws IOException {
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
                    Assertions.assertArrayEquals(
                            HelloWorldTestUtils.hello_world_byte_array(),
                            in.readAllBytes()
                    );
                }
            }
        }
    }
}
