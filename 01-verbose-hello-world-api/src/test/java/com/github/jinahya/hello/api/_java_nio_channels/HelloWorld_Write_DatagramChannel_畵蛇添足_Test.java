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
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.*;

/**
 * A class for testing {@link HelloWorld#write(DatagramChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@畵蛇添足
@Slf4j
class HelloWorld_Write_DatagramChannel_畵蛇添足_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        HelloWorldTestUtils.write_datagramchannel_writes_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class DatagramSocket_Server_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var address = new CompletableFuture<SocketAddress>();
            Thread.ofVirtual().start(() -> {
                try (var server = new DatagramSocket(
                        new InetSocketAddress(InetAddress.getLocalHost(), 0))) {
                    assert server.isBound();
                    address.complete(server.getLocalSocketAddress());
                    final var packet = new DatagramPacket(new byte[HelloWorld.BYTES],
                                                          HelloWorld.BYTES);
                    server.receive(packet);
                    log.debug("received: {}",
                              new String(packet.getData(), StandardCharsets.US_ASCII));
                } catch (final IOException ioe) {
                    log.error("failed to start/receive server");
                    address.completeExceptionally(ioe);
                }
            });
            return address;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                assert client.isBlocking();
                client.connect(startServer().join());
                service().write(client);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(startServer().join());
                service().write(client);
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Blocking_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var address = new CompletableFuture<SocketAddress>();
            Thread.ofVirtual().start(() -> {
                try (var server = DatagramChannel.open()) {
                    server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                    address.complete(server.getLocalAddress());
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    server.receive(buffer);
                    log.debug("received: {}", StandardCharsets.US_ASCII.decode(buffer.flip()));
                } catch (final IOException ioe) {
                    log.error("failed to start/receive server");
                    address.completeExceptionally(ioe);
                }
            });
            return address;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(startServer().join());
                service().write(client);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(startServer().join());
                service().write(client);
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Nonblocking_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var address = new CompletableFuture<SocketAddress>();
            Thread.ofVirtual().start(() -> {
                try (var selector = Selector.open();
                     var server = DatagramChannel.open()) {
                    server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                    address.complete(server.getLocalAddress());
                    server.configureBlocking(false);
                    final var key = server.register(selector, SelectionKey.OP_READ);
                    selector.select();
                    assert selector.selectedKeys().iterator().next() == key;
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    server.receive(buffer);
                    log.debug("received: {}", StandardCharsets.US_ASCII.decode(buffer.flip()));
                } catch (final IOException ioe) {
                    log.error("failed to start/receive server");
                    address.completeExceptionally(ioe);
                }
            });
            return address;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(startServer().join());
                service().write(client);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(startServer().join());
                service().write(client);
            }
        }
    }
}
