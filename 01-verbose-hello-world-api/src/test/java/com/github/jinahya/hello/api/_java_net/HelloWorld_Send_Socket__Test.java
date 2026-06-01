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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("send(socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_Socket__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        send_socket_sends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("socket")
    @Nested
    class SocketTest {

        @DisplayName(
                "should send <hello-world-bytes> through a real <Socket> over <InetSocketAddress>")
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

        @DisplayName("""
                should send <hello-world-bytes> through a real <Socket>
                over <UnixDomainSocketAddress>""")
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

        @DisplayName("""
                should round-trip <hello-world-bytes> through a real <Socket> echo server
                over <InetSocketAddress>""")
        @Test
        void ___InetSocketAddress() throws IOException {
            try (var server = new ServerSocket()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                log.debug("[server] bound: {}", server.getLocalSocketAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        final var bytes = accepted.getInputStream().readNBytes(HelloWorld.BYTES);
                        assert bytes.length == HelloWorld.BYTES;
                        accepted.getOutputStream().write(bytes);
                        accepted.getOutputStream().flush();
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = new Socket()) {
                    client.connect(server.getLocalSocketAddress());
                    log.debug("[client] connected to : {}", client.getRemoteSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                    final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                    assertArrayEquals(hello_world_byte_array(), bytes);
                }
            }
        }

        @DisplayName("""
                should round-trip <hello-world-bytes> through a real <Socket> echo server
                over <UnixDomainSocketAddress>""")
        @Disabled("unsupported")
        @Test
        void ___UnixDomainSocketAddress() throws IOException {
            final var tempFile = File.createTempFile("tmp", null, tempDir);
            final var deleted = tempFile.delete();
            assert deleted;
            try (var server = ServerSocketChannel.open(StandardProtocolFamily.UNIX).socket()) {
                server.bind(UnixDomainSocketAddress.of(tempFile.getPath()));
                log.debug("[server] bound: {}", server.getLocalSocketAddress());
                Thread.ofPlatform().start(() -> {
                    try (var accepted = server.accept()) {
                        final var bytes = accepted.getInputStream().readNBytes(HelloWorld.BYTES);
                        assert bytes.length == HelloWorld.BYTES;
                        accepted.getOutputStream().write(bytes);
                        accepted.getOutputStream().flush();
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = SocketChannel.open(StandardProtocolFamily.UNIX).socket()) {
                    client.connect(server.getLocalSocketAddress());
                    log.debug("[client] connected to : {}", client.getRemoteSocketAddress());
                    service().send(client);
                    client.getOutputStream().flush();
                    final var bytes = client.getInputStream().readNBytes(HelloWorld.BYTES);
                    assertArrayEquals(hello_world_byte_array(), bytes);
                }
            }
        }
    }
}
