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
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._java_nio_channels._Channels_TestUtils.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link HelloWorld#write(WritableByteChannel) write(channel)} method with
 * real implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_NotForPublishing
@DisplayName("HelloWorld.write(WritableByteChannel) / extras")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_WritableByteChannel__Test extends HelloWorld__Test {

    /**
     * The {@link TempDir} shared by tests in this class.
     */
    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.write(channel)} to write the {@code hello-world-bytes} via the channel
     * before each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void beforeEach() throws IOException {
        write_writablebytechannel_writes_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("pipe")
    @Nested
    class Pipe_Test {

        /**
         * Verifies that the method writes {@code hello-world-bytes} through a {@link Pipe}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("happy path")
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

    @DisplayName("socket channel")
    @Nested
    class SocketChannel_Test {

        /**
         * Verifies that the method writes {@code hello-world-bytes} through a {@link SocketChannel}
         * over a loopback address.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("loopback")
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

    @DisplayName("echo server")
    @Nested
    class EchoServer_Test {

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server over an
         * {@link InetSocketAddress}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("INET")
        @Test
        void __INET() throws IOException {
            try (var server = ServerSocketChannel.open(StandardProtocolFamily.INET)) {
                server.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        copy1(ByteBuffer.allocate(1), accepted, accepted);
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                });
                try (var client = SocketChannel.open()) {
                    client.connect(server.getLocalAddress());
                    log.debug("[client] connected to {}", client.getRemoteAddress());
                    service().write(client).shutdownOutput();
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    for (int r; dst.hasRemaining(); ) {
                        r = client.read(dst);
                        assert r != -1;
                    }
                    assertEquals(hello_world_byte_buffer(), dst.flip());
                }
            }
        }

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server over an
         * {@link Inet6Address}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("INET6")
        @DisabledIfSystemProperty(named = "java.net.preferIPv4Stack", matches = "true",
                                  disabledReason = "IPv6 disabled by preferIPv4Stack=true")
        @Test
        void __INET6() throws IOException {
            try (var server = ServerSocketChannel.open(StandardProtocolFamily.INET6)) {
                server.bind(new InetSocketAddress(InetAddress.getByName("::1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        copy1(ByteBuffer.allocate(1), accepted, accepted);
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                });
                try (var client = SocketChannel.open(StandardProtocolFamily.INET6)) {
                    client.connect(server.getLocalAddress());
                    log.debug("[client] connected to {}", client.getRemoteAddress());
                    service().write(client).shutdownOutput();
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    for (int r; dst.hasRemaining(); ) {
                        r = client.read(dst);
                        assert r != -1;
                    }
                    assertEquals(hello_world_byte_buffer(), dst.flip());
                }
            }
        }

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server over a
         * {@link UnixDomainSocketAddress}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("UNIX")
        @Test
        void __UNIX() throws IOException {
            try (var server = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
                server.bind(null);
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        copy1(ByteBuffer.allocate(1), accepted, accepted);
                    } catch (final IOException ioe) {
                        log.error("failed to read", ioe);
                    }
                });
                try (var client = SocketChannel.open(StandardProtocolFamily.UNIX)) {
                    client.connect(server.getLocalAddress());
                    log.debug("[client] connected to {}", client.getRemoteAddress());
                    service().write(client).shutdownOutput();
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
