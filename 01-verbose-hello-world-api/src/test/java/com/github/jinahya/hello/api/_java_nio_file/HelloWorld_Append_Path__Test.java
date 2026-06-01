package com.github.jinahya.hello.api._java_nio_file;

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
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("append(path)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_Path__Test extends HelloWorld__Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        append_path_appends_hello_world(service());
    }

    @DisplayName("should append <hello-world-bytes> to a real <Path>")
    @Test
    void __() throws Exception {
        final var path = Files.createTempFile(tempDir, null, null);
        final var size = Files.size(writeSome(path));
        service().append(path);
        assertEquals(size + HelloWorld.BYTES, Files.size(path));
        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            channel.position(size);
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            while (buffer.hasRemaining() ) {
                channel.read(buffer);
            }
            assertEquals(HELLO_WORLD_STRING, US_ASCII.decode(buffer.flip()).toString());
        }
    }
}
