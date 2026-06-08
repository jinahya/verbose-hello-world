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
import org.junit.jupiter.api.condition.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;

/**
 * A class for exploring {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method
 * with real implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_AsynchronousByteChannel__Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __stubService() throws ExecutionException, InterruptedException {
        write_asynchornousbytechannel_writes_hello_world(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("echo server")
    @Nested
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    class EchoServer_Test {

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server over an
         * {@code IPv4} address.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("should write <hello-world-bytes> to an <echo server> over an <IPv4> address")
        @Test
        void __INET() throws Exception {
            try (final var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        try (final var client = server.accept().get()) {
                            log.debug("[server] accepted from {}", client.getRemoteAddress());
                            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                            while (buffer.hasRemaining()) {
                                if (client.read(buffer).get() == -1) {
                                    throw new EOFException("unexpected end of stream");
                                }
                            }
                            log.debug("[server] received from {}", client.getRemoteAddress());
                            for (buffer.flip(); buffer.hasRemaining(); ) {
                                client.write(buffer).get();
                            }
                            log.debug("[server] sent to {}", client.getRemoteAddress());
                        }
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                try (final var client = AsynchronousSocketChannel.open()) {
                    client.connect(server.getLocalAddress()).get();
                    log.debug("[client] connected to {}", client.getRemoteAddress());
                    service().write(client);
                    log.debug("[client] sent to {}", client.getRemoteAddress());
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (dst.hasRemaining()) {
                        if (client.read(dst).get() == -1) {
                            break;
                        }
                    }
                    log.debug("[client] received from {}", client.getRemoteAddress());
                }
            }
        }

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server over an
         * {@code IPv6} address.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("should write <hello-world-bytes> to an <echo server> over an <IPv6> address")
        @DisabledIfSystemProperty(named = "java.net.preferIPv4Stack", matches = "true",
                                  disabledReason = "IPv6 disabled by preferIPv4Stack=true")
        @Test
        void __INET6() throws Exception {
            try (final var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getByName("::1"), 0));
                log.debug("[server] bound to {}", server.getLocalAddress());
                Thread.ofPlatform().start(() -> {
                    try {
                        try (final var client = server.accept().get()) {
                            log.debug("[server] accepted from {}", client.getRemoteAddress());
                            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                            while (buffer.hasRemaining()) {
                                if (client.read(buffer).get() == -1) {
                                    throw new EOFException("unexpected end of stream");
                                }
                            }
                            log.debug("[server] received from {}", client.getRemoteAddress());
                            for (buffer.flip(); buffer.hasRemaining(); ) {
                                client.write(buffer).get();
                            }
                            log.debug("[server] sent to {}", client.getRemoteAddress());
                        }
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                try (final var client = AsynchronousSocketChannel.open()) {
                    client.connect(server.getLocalAddress()).get();
                    log.debug("[client] connected to {}", client.getRemoteAddress());
                    service().write(client);
                    log.debug("[client] sent to {}", client.getRemoteAddress());
                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (dst.hasRemaining()) {
                        if (client.read(dst).get() == -1) {
                            break;
                        }
                    }
                    log.debug("[client] received from {}", client.getRemoteAddress());
                }
            }
        }

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server over a UNIX
         * domain address.
         */
        @DisplayName(
                "should write <hello-world-bytes> to an <echo server> over a <UNIX domain> address")
        @Disabled("AsynchronousServerSocketChannel does not support UNIX domain")
        @Test
        void __UNIX() {
            // AsynchronousServerSocketChannel has no open(ProtocolFamily) overload
        }
    }
}
