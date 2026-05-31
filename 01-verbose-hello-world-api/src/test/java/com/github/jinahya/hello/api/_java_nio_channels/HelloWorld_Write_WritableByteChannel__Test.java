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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api._Java_Nio_Channels_TestUtils.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_WritableByteChannel__Test extends HelloWorld__Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        write_writablebytechannel_writes_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class Pipe_Test {

        @Test
        void __() throws IOException {
            final var pipe = Pipe.open();
            Thread.ofPlatform().start(() -> {
                try (var sink = pipe.sink()) {
                    service().write(sink);
                } catch (final IOException ioe) {
                    log.error("failed to write", ioe);
                }
            });
            try (var source = pipe.source()) {
                final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                for (int r; dst.hasRemaining(); ) {
                    r = source.read(dst);
                    assert r != -1;
                }
                assertEquals(hello_world_byte_buffer(), dst.flip());
            }
        }
    }

    @Nested
    class SocketChannel_Test {

        @Test
        void __() throws IOException {
            try (var server = ServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                        for (int r; dst.hasRemaining(); ) {
                            r = accepted.read(dst);
                            assert r != -1;
                        }
                        final var string = US_ASCII.decode(dst.flip()).toString();
                        assertEquals(HELLO_WORLD_STRING, string);
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                });
                try (var client = SocketChannel.open()) {
                    client.connect(server.getLocalAddress());
                    service().write(client);
                }
            }
        }
    }

    @Nested
    class EchoServer_Test {

        @Test
        void __InetSocketAddress() throws IOException {
            try (var server = ServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                log.debug("[server] bound: {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try (var client = server.accept()) {
                        final var buf = ByteBuffer.allocate(1);
                        while (client.read(buf.clear()) != -1) {
                            for (buf.flip(); buf.hasRemaining(); ) {
                                client.write(buf);
                            }
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                });
                try (var client = SocketChannel.open()) {
                    client.connect(server.getLocalAddress());
                    log.debug("[client] connected to : {}", client.getRemoteAddress());
                    service().write(client);
                    client.shutdownOutput();
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    for (int r; dst.hasRemaining(); ) {
                        r = client.read(dst);
                        assert r != -1;
                    }
                    assertEquals(hello_world_byte_buffer(), dst.flip());
                }
            }
        }

        @Test
        void ___UnixDomainSocketAddress() throws IOException {
            try (var server = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
                server.bind(null);
                log.debug("[server] bound: {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try (var client = server.accept()) {
                        final var buf = ByteBuffer.allocate(1);
                        buf.clear();
                        while (client.read(buf) != -1) {
                            client.write(buf.flip());
                            buf.compact();
                        }
                        for (buf.flip(); buf.hasRemaining(); ) {
                            client.write(buf);
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                });
                try (var client = SocketChannel.open(StandardProtocolFamily.UNIX)) {
                    client.connect(server.getLocalAddress());
                    log.debug("[client] connected to : {}", client.getRemoteAddress());
                    service().write(client);
                    client.shutdownOutput();
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    for (int r; dst.hasRemaining(); ) {
                        r = client.read(dst);
                        assert r != -1;
                    }
                    assertEquals(hello_world_byte_buffer(), dst.flip());
                }
            }
        }
    }
}
