package com.github.jinahya.hello.api._java_net;

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
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link HelloWorld#send(Socket) send(socket)} method with real
 * implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideFromPublishing
@DisplayName("HelloWorld.send(Socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_Socket__Test extends HelloWorld__Test {

    /**
     * The {@link TempDir} shared by tests in this class.
     */
    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.send(socket)} to send the {@code hello-world-bytes} before each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __() throws IOException {
        send_socket_sends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("Socket")
    @Nested
    class SocketTest {

        /**
         * Verifies that the method sends {@code hello-world-bytes} through a real {@link Socket}
         * over {@link InetSocketAddress}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("InetSocketAddress")
        @Test
        void ___InetSocketAddress() throws IOException {
            try (var server = new ServerSocket()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                Thread.ofPlatform().start(() -> {
                    try (var client = server.accept()) {
                        final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                        assert bytes.length == HelloWorld.BYTES;
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = new Socket()) {
                    client.connect(server.getLocalSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                }
            }
        }

        /**
         * Verifies that the method sends {@code hello-world-bytes} through a real {@link Socket}
         * over {@link UnixDomainSocketAddress}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("UnixDomainSocketAddress")
        @Disabled("unsupported")
        @Test
        void ___UnixDomainSocketAddress() throws IOException {
            final var tempFile = File.createTempFile("tmp", null, tempDir);
            final var deleted = tempFile.delete();
            assert deleted;
            try (var server = ServerSocketChannel.open(StandardProtocolFamily.UNIX).socket()) {
                server.bind(UnixDomainSocketAddress.of(tempFile.getPath()));
                Thread.ofPlatform().start(() -> {
                    try (var client = server.accept()) {
                        final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                        assert bytes.length == HelloWorld.BYTES;
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = SocketChannel.open(StandardProtocolFamily.UNIX).socket()) {
                    client.connect(server.getLocalSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                }
            }
        }
    }

    @DisplayName("echo server")
    @Nested
    class EchoServer_Test {

        /**
         * Verifies that the method round-trips {@code hello-world-bytes} through a real
         * {@link Socket} echo server over {@link InetSocketAddress}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("INET")
        @Test
        void __INET() throws IOException {
            try (var server = new ServerSocket()) {
                server.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0));
                log.debug("[server] bound: {}", server.getLocalSocketAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        log.debug("[server] accepted from {}", accepted.getRemoteSocketAddress());
                        final var bytes = accepted.getInputStream().readNBytes(HelloWorld.BYTES);
                        log.debug("[server] {} bytes read", bytes.length);
                        assert bytes.length == HelloWorld.BYTES;
                        accepted.getOutputStream().write(bytes);
                        accepted.getOutputStream().flush();
                        log.debug("[server] {} bytes written", bytes.length);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = new Socket()) {
                    client.connect(server.getLocalSocketAddress());
                    log.debug("[client] connected to {}", client.getRemoteSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                    log.debug("[client] {} bytes written", HelloWorld.BYTES);
                    final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                    log.debug("[client] {} bytes read", bytes.length);
                    assertArrayEquals(hello_world_byte_array(), bytes);
                }
            }
        }

        /**
         * Verifies that the method round-trips {@code hello-world-bytes} through a real
         * {@link Socket} echo server over {@link Inet6Address}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("INET6")
        @Test
        void __INET6() throws IOException {
            try (var server = new ServerSocket()) {
                server.bind(new InetSocketAddress(InetAddress.getByName("::1"), 0));
                log.debug("[server] bound: {}", server.getLocalSocketAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        log.debug("[server] accepted from {}", accepted.getRemoteSocketAddress());
                        final var bytes = accepted.getInputStream().readNBytes(HelloWorld.BYTES);
                        log.debug("[server] {} bytes read", bytes.length);
                        assert bytes.length == HelloWorld.BYTES;
                        accepted.getOutputStream().write(bytes);
                        accepted.getOutputStream().flush();
                        log.debug("[server] {} bytes written", bytes.length);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = new Socket()) {
                    client.connect(server.getLocalSocketAddress());
                    log.debug("[client] connected to {}", client.getRemoteSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                    log.debug("[client] {} bytes written", HelloWorld.BYTES);
                    final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                    log.debug("[client] {} bytes read", bytes.length);
                    assertArrayEquals(hello_world_byte_array(), bytes);
                }
            }
        }

        /**
         * Verifies that invoking {@code socket()} on a {@link ServerSocketChannel} opened with
         * {@link StandardProtocolFamily#UNIX} throws an {@link UnsupportedOperationException}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("UNIX / ServerSocketChannel.socket() throws UOE")
        @Test
        void __UNIX1() throws IOException {
            final var tempFile = File.createTempFile("tmp", null, tempDir);
            final var deleted = tempFile.delete();
            assert deleted;
            assertThrows(UnsupportedOperationException.class, () -> {
                try (var server = ServerSocketChannel.open(StandardProtocolFamily.UNIX).socket()) {
                }
            });
        }

        /**
         * Verifies that binding a real {@link ServerSocket} to a {@link UnixDomainSocketAddress}
         * throws an {@link IllegalArgumentException}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("UNIX / ServerSocket.bind(UnixDomainSocketAddress) throws IAE")
        @Test
        void __UNIX2() throws IOException {
            final var tempFile = File.createTempFile("tmp", null, tempDir);
            final var deleted = tempFile.delete();
            assert deleted;
            try (var server = new ServerSocket()) {
                assertThrows(IllegalArgumentException.class, () -> {
                    server.bind(UnixDomainSocketAddress.of(tempFile.getPath()));
                });
            }
        }
    }
}
