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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

@畵蛇添足
@Slf4j
class HelloWorld_Write_AsynchronousFileChannel_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws ExecutionException, InterruptedException {
        HelloWorldTestUtils.write_asynchornousfilechannel_position_writes_hello_world(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var path = Files.createTempFile(tempDir, null, null);
        var position = ThreadLocalRandom.current().nextLong(0L, 128L);
        // ------------------------------------------------------------------------------------ when
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            service().write(channel, position).force(false);
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
