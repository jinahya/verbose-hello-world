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
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;

import static com.github.jinahya.hello.api.HelloWorld.*;
import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static java.io.File.*;
import static java.lang.String.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.concurrent.ThreadLocalRandom.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.junit.jupiter.api.Assertions.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_DataOutput__Test extends HelloWorld__Test {

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

    @Nested
    class UTF8_Test {

        @ValueSource(strings = {
                HELLO_WORLD_STRING,
                "홍길동",
                "\uD83C\uDD30",
                "\uD83D\uDE00"
        })
        @ParameterizedTest
        void __(final String expected) throws IOException {
            try (var baos = new ByteArrayOutputStream();
                 final var dos = new DataOutputStream(baos)) {
                dos.writeUTF(expected);
                baos.flush();
                final var bytes = baos.toByteArray();
                System.out.printf("%-50s %s%n",
                                  range(0, bytes.length)
                                          .mapToObj(i -> format("%02x", bytes[i]))
                                          .collect(joining(" ", "", "")), expected);
                try (var bais = new ByteArrayInputStream(bytes);
                     final var dis = new DataInputStream(bais)) {
                    final var actual = dis.readUTF();
                    assertEquals(expected, actual);
                }
            }
        }
    }
}
