package com.github.jinahya.hello.api._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;

/**
 * A class for exploring
 * {@link HelloWorld#send(DatagramChannel, SocketAddress) send(channel, target)} method with real
 * implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("HelloWorld.send(DatagramChannel, target) / extras")
@Slf4j
class HelloWorld_Send_DatagramChannel_Target__Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs the service's {@code send(socket, target)} and {@code send(channel, target)} so they
     * send the {@code hello-world-bytes} before each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __stubService() throws IOException {
        send_datagramsocket_socketaddress_sends_hello_world_packet(service());
        send_channel_target_sends_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------

    @DisplayName("echo server")
    @Nested
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    class EchoServer_Test {

        /**
         * Verifies that the method sends {@code hello-world-bytes} to an echo server over an
         * {@code IPv4} address.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("INET")
        @Test
        void __INET() throws IOException {
            try (final var server = DatagramChannel.open(StandardProtocolFamily.INET)) {
                server.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        server.configureBlocking(ThreadLocalRandom.current().nextBoolean());
                        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                        while (buffer.hasRemaining()) {
                            final var address = server.receive(buffer);
                            if (address == null) {
                                continue;
                            }
                            log.debug("[server] received from {}", address);
                            for (buffer.flip(); buffer.hasRemaining(); ) {
                                server.send(buffer, address);
                            }
                            log.debug("[server] sent to {}", address);
                        }
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open(StandardProtocolFamily.INET)) {
                    final var target = server.getLocalAddress();
                    client.configureBlocking(ThreadLocalRandom.current().nextBoolean());
                    service().send(client, server.getLocalAddress());
                    log.debug("[client] sent to {}", target);
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (dst.hasRemaining()) {
                        final var address = client.receive(dst);
                        if (address == null) {
                            continue;
                        }
                        log.debug("[client] received from {}", address);
                    }
                }
            }
        }

        /**
         * Verifies that the method sends {@code hello-world-bytes} to an echo server over an
         * {@code IPv6} address.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("INET6")
        @DisabledIfSystemProperty(named = "java.net.preferIPv4Stack", matches = "true",
                                  disabledReason = "IPv6 disabled by preferIPv4Stack=true")
        @Test
        void __INET6() throws IOException {
            try (final var server = DatagramChannel.open(StandardProtocolFamily.INET6)) {
                server.bind(new InetSocketAddress(InetAddress.getByName("::1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        server.configureBlocking(ThreadLocalRandom.current().nextBoolean());
                        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                        while (buffer.hasRemaining()) {
                            final var address = server.receive(buffer);
                            if (address == null) {
                                continue;
                            }
                            log.debug("[server] received from {}", address);
                            for (buffer.flip(); buffer.hasRemaining(); ) {
                                server.send(buffer, address);
                            }
                            log.debug("[server] sent to {}", address);
                        }
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open(StandardProtocolFamily.INET6)) {
                    final var target = server.getLocalAddress();
                    client.configureBlocking(ThreadLocalRandom.current().nextBoolean());
                    service().send(client, target);
                    log.debug("[client] sent to {}", target);
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (dst.hasRemaining()) {
                        final var address = client.receive(dst);
                        if (address == null) {
                            continue;
                        }
                        log.debug("[client] received from {}", address);
                    }
                }
            }
        }

        /**
         * Verifies that the method sends {@code hello-world-bytes} to an echo server over a UNIX
         * domain address.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("UNIX")
        @Disabled("not supported")
        @Test
        void __UNIX() throws IOException {
            try (final var server = DatagramChannel.open(StandardProtocolFamily.UNIX)) {
                server.bind(null);
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        server.configureBlocking(ThreadLocalRandom.current().nextBoolean());
                        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                        while (buffer.hasRemaining()) {
                            final var address = server.receive(buffer);
                            if (address == null) {
                                continue;
                            }
                            log.debug("[server] received from {}", address);
                            for (buffer.flip(); buffer.hasRemaining(); ) {
                                server.send(buffer, address);
                            }
                            log.debug("[server] sent to {}", address);
                        }
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open(StandardProtocolFamily.UNIX)) {
                    final var target = server.getLocalAddress();
                    client.configureBlocking(ThreadLocalRandom.current().nextBoolean());
                    service().send(client, target);
                    log.debug("[client] sent to {}", target);
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (dst.hasRemaining()) {
                        final var address = client.receive(dst);
                        if (address == null) {
                            continue;
                        }
                        log.debug("[client] received from {}", address);
                    }
                }
            }
        }
    }
}
