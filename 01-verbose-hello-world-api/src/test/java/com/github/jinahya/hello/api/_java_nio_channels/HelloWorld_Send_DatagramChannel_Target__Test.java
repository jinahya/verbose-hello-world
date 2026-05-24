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

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.mockito.Mockito.*;

@Slf4j
class HelloWorld_Send_DatagramChannel_Target__Test extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        send_datagramchannel_socketaddress_sends_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class DatagramSocket_Server_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var future = new CompletableFuture<SocketAddress>();
            Thread.ofVirtual().start(() -> {
                try (var server = new DatagramSocket(
                        new InetSocketAddress(InetAddress.getLoopbackAddress(), 0))) {
                    future.complete(server.getLocalSocketAddress());
                    final var packet = new DatagramPacket(new byte[HelloWorld.BYTES],
                                                          HelloWorld.BYTES);
                    server.receive(packet);
                    final var decoded = new String(packet.getData(), StandardCharsets.US_ASCII);
                    log.debug("'{}' received from {}", decoded, packet.getSocketAddress());
                } catch (final IOException ioe) {
                    future.completeExceptionally(ioe);
                }
            });
            return future;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = spy(DatagramChannel.open())) {
                assert client.isBlocking();
                service().send(client, startServer().join());
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                service().send(client, startServer().join());
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Blocking_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var future = new CompletableFuture<SocketAddress>();
            Thread.ofVirtual().start(() -> {
                try (var server = DatagramChannel.open()) {
                    server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                    future.complete(server.getLocalAddress());
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    final var address = server.receive(buffer);
                    final var decoded = StandardCharsets.US_ASCII.decode(buffer.flip());
                    log.debug("'{}' received from {}", decoded, address);
                } catch (final IOException ioe) {
                    future.completeExceptionally(ioe);
                }
            });
            return future;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = spy(DatagramChannel.open())) {
                service().send(client, startServer().join());
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                service().send(client, startServer().join());
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Nonblocking_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var future = new CompletableFuture<SocketAddress>();
            Thread.ofVirtual().start(() -> {
                try (var selector = Selector.open();
                     var server = DatagramChannel.open()) {
                    server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                    future.complete(server.getLocalAddress());
                    server.configureBlocking(false);
                    final var key = server.register(selector, SelectionKey.OP_READ);
                    while (selector.select() == 0) ;
                    assert selector.selectedKeys().iterator().next() == key;
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    final var address = server.receive(buffer);
                    final var decoded = StandardCharsets.US_ASCII.decode(buffer.flip());
                    log.debug("'{}' received from {}", decoded, address);
                } catch (final IOException ioe) {
                    future.completeExceptionally(ioe);
                }
            });
            return future;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = spy(DatagramChannel.open())) {
                service().send(client, startServer().join());
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = spy(DatagramChannel.open())) {
                service().send(client, startServer().join());
            }
        }
    }
}
