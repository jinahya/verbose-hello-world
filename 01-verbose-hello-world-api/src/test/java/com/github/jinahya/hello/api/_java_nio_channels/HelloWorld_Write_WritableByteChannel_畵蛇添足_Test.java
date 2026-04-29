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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_WritableByteChannel_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        HelloWorldTestUtils.write_writablebytechannel_writes_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class FileChannelTest {

        @Test
        void __() throws IOException {
            final var path = Files.createTempFile(tempDir, null, null);
            try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
                service().write(channel);
                channel.force(true);
            }
            final var bytes = Files.readAllBytes(path);
            log.debug("string: {}", new String(bytes, StandardCharsets.US_ASCII));
        }
    }

    @Nested
    class SocketChannelTest {

        @Test
        void __() throws IOException {
//            final var address = new CompletableFuture<SocketAddress>();
//            try (var server = ServerSocketChannel.open()) {
//                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
//            }
//            final var path = Files.createTempFile(tempDir, null, null);
//            try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
//                service().write(channel);
//                channel.force(true);
//            }
//            final var bytes = Files.readAllBytes(path);
//            log.debug("string: {}", new String(bytes, StandardCharsets.US_ASCII));
        }
    }
}
