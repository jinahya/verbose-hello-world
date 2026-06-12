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

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#send(DatagramSocket) send(socket)} method with real
 * implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(socket)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Send_DatagramSocket__Test extends HelloWorld__Test {

    /**
     * Stubs {@code service.send(socket)} to send the {@code hello-world-bytes} through the given
     * connected socket before each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __stubService() throws IOException {
        doAnswer(i -> {
            final var socket = i.getArgument(0, DatagramSocket.class);
            final var packet = new DatagramPacket(hello_world_byte_array(), HelloWorld.BYTES);
            socket.send(packet);
            return socket;
        }).when(service()).send(argThat(DatagramSocket::isConnected));
    }

    /**
     * Verifies that the method sends {@code hello-world-bytes} through a real connected
     * {@link DatagramSocket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should send <hello-world-bytes> through a real connected <DatagramSocket>")
    @Test
    void __() throws IOException {
        try (var server = new DatagramSocket(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0))) {
            Thread.ofPlatform().start(() -> {
                try {
                    final DatagramPacket packet = new DatagramPacket(
                            new byte[HelloWorld.BYTES], // <buf>
                            HelloWorld.BYTES            // <length>
                    );
                    server.receive(packet);
                    final var decoded = new String(
                            packet.getData(),         // <bytes>
                            packet.getOffset(),       // <offset>
                            packet.getLength(),       // <length>
                            StandardCharsets.US_ASCII // <charset>
                    );
                    log.debug("'{}' received from {}", decoded, packet.getSocketAddress());
                } catch (final IOException ioe) {
                    throw new UncheckedIOException(ioe);
                }
            });
            try (var client = new DatagramSocket()) {
                client.connect(server.getLocalSocketAddress());
                service().send(client);
            }
        }
    }

    @DisplayName("echo server")
    @Nested
    class EchoServer_Test {

        /**
         * Verifies that the client invokes {@code send(socket)} to a connected server, and the
         * server echoes the bytes back to the sender.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("""
                client should <send(socket)> to a connected server;
                server should echo the bytes back to the sender""")
        @Test
        void __() throws IOException {
            try (var server = new DatagramSocket(null)) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                Thread.ofPlatform().start(() -> {
                    try {
                        final var packet = new DatagramPacket(
                                new byte[HelloWorld.BYTES], HelloWorld.BYTES
                        );
                        server.receive(packet);
                        assert packet.getOffset() == 0;
                        assert packet.getLength() == HelloWorld.BYTES;
                        assert packet.getSocketAddress() != null;
                        server.send(packet);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                });
                try (var client = new DatagramSocket()) {
                    client.connect(server.getLocalSocketAddress());
                    service().send(client);
                    final var packet = new DatagramPacket(
                            new byte[HelloWorld.BYTES], HelloWorld.BYTES
                    );
                    client.receive(packet);
                    assert packet.getOffset() == 0;
                    assert packet.getLength() == HelloWorld.BYTES;
                    assert packet.getSocketAddress() != null;
                    final var decoded = new String(packet.getData(), StandardCharsets.US_ASCII);
                    log.debug("'{}' echoed from {}", decoded, packet.getSocketAddress());
                }
            }
        }
    }
}
