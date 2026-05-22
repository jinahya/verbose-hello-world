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

import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.github.jinahya.hello.api.HelloWorld.BYTES;
import static com.github.jinahya.hello.api.HelloWorldTestConstants.HELLO_WORLD_STRING;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.write_dataoutput_writes_hello_world_bytes;
import static java.io.File.createTempFile;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_DataOutput__Test extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        write_dataoutput_writes_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class DataOutputStream_Test {

        @Test
        void __() throws IOException {
            try (var baos = new ByteArrayOutputStream();
                 var dos = new DataOutputStream(baos)) {
                service().write((DataOutput) dos);
                dos.flush();
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     var dais = new DataInputStream(bais)) {
                    final var bytes = dais.readAllBytes();
                    final var string = new String(bytes, US_ASCII);
                    assertEquals(HELLO_WORLD_STRING, string);
                }
            }
        }
    }

    @Nested
    class RandomAccessFile_Test {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var tempFile = createTempFile("tmp", "tmp", tempDir);
            final var pos = current().nextLong(0L, 128L);
            // -------------------------------------------------------------------------------- when
            try (var file = new RandomAccessFile(tempFile, "rw")) {
                file.seek(pos);
                service().write((DataOutput) file);
                file.getFD().sync();
            }
            // -------------------------------------------------------------------------------- then
            assertEquals(pos + BYTES, tempFile.length());
            try (var file = new RandomAccessFile(tempFile, "r")) {
                file.seek(pos);
                final var b = new byte[BYTES];
                file.readFully(b);
                log.debug("read: {}", new String(b, US_ASCII));
            }
        }
    }
}
