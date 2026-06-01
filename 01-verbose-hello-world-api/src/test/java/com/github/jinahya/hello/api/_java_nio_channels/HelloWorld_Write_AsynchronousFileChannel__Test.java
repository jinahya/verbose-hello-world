package com.github.jinahya.hello.api._java_nio_channels;

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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;

@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_Write_AsynchronousFileChannel__Test extends HelloWorld__Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __stubService() throws ExecutionException, InterruptedException {
        write_asynchronousfilechannel_long_writes_hello_world(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName(
            "should write <hello-world-bytes> to a real <AsynchronousFileChannel> at a <position>")
    @Test
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var path = Files.createTempFile(tempDir, null, null);
        var position = ThreadLocalRandom.current().nextLong(0L, 128L);
        // ------------------------------------------------------------------------------------ when
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            service().write(channel, position);
        }
        // ------------------------------------------------------------------------------------ then
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            while (buffer.hasRemaining()) {
                position += channel.read(buffer, position).get();
            }
            log.debug("read: {}", StandardCharsets.US_ASCII.decode(buffer.flip()));
        }
    }
}
