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
import java.nio.charset.*;
import java.nio.file.*;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_Path__Test extends HelloWorldTest {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        append_path_appends_hello_world(service());
    }

    @Test
    void __() throws Exception {
        final var path = Files.createTempFile(tempDir, null, null);
        writeSome(path);
        final var size = Files.size(path);
        service().append(path);
        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            channel.position(size);
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            for (int r; buffer.hasRemaining(); ) {
                r = channel.read(buffer);
                assert r != -1;
            }
            final var decoded = StandardCharsets.US_ASCII.decode(buffer.flip()).toString();
            assertEquals(HelloWorldTestConstants.HELLO_WORLD_STRING, decoded);
        }
    }
}
