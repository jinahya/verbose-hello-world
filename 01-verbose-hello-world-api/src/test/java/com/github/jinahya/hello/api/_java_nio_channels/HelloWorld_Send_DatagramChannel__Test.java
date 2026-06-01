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
 * A class for testing {@link HelloWorld#send(DatagramChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("send(channel)")
@Slf4j
class HelloWorld_Send_DatagramChannel__Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __stubService() throws IOException {
        send_datagramsocket_sends_hello_world_packet(service());
        write_writablebytechannel_writes_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("echo server")
    @Nested
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    class EchoServer_Test {

        private void doServer(final DatagramChannel server) throws IOException {
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
        }

        private void doClient(final DatagramChannel client, final SocketAddress target)
                throws IOException {
            client.connect(target);
            log.debug("[client] connected to {}", client.getRemoteAddress());
            client.configureBlocking(ThreadLocalRandom.current().nextBoolean());
            service().send(client);
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

        @DisplayName(
                "should send <hello-world-bytes> to an <echo server> over a <loopback> address")
        @Test
        void __() throws IOException {
            try (final var server = DatagramChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        doServer(server);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open()) {
                    doClient(client, server.getLocalAddress());
                }
            }
        }

        @DisplayName("should send <hello-world-bytes> to an <echo server> over an <IPv4> address")
        @Test
        void __INET() throws IOException {
            try (final var server = DatagramChannel.open(StandardProtocolFamily.INET)) {
                server.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        doServer(server);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open(StandardProtocolFamily.INET)) {
                    doClient(client, server.getLocalAddress());
                }
            }
        }

        @DisplayName("should send <hello-world-bytes> to an <echo server> over an <IPv6> address")
        @DisabledIfSystemProperty(named = "java.net.preferIPv4Stack", matches = "true",
                                  disabledReason = "IPv6 disabled by preferIPv4Stack=true")
        @Test
        void __INET6() throws IOException {
            try (final var server = DatagramChannel.open(StandardProtocolFamily.INET6)) {
                server.bind(new InetSocketAddress(InetAddress.getByName("::1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        doServer(server);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open(StandardProtocolFamily.INET6)) {
                    doClient(client, server.getLocalAddress());
                }
            }
        }

        @DisplayName(
                "should send <hello-world-bytes> to an <echo server> over a <UNIX domain> address")
        @Disabled("not supported")
        @Test
        void __UNIX() throws IOException {
            try (final var server = DatagramChannel.open(StandardProtocolFamily.UNIX)) {
                server.bind(null);
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        doServer(server);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                try (final var client = DatagramChannel.open(StandardProtocolFamily.UNIX)) {
                    doClient(client, server.getLocalAddress());
                }
            }
        }
    }
}
