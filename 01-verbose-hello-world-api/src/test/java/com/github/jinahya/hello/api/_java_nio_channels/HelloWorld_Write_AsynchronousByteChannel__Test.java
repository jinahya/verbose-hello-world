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

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import java.util.concurrent.atomic.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method
 * with real implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_NotForPublishing
@DisplayName("HelloWorld.write(AsynchronousByteChannel) / extras")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_AsynchronousByteChannel__Test extends HelloWorld__Test {

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code service.write(channel)} to write the {@code hello-world-bytes} through the given
     * channel before each test.
     *
     * @throws ExecutionException   if an error occurs.
     * @throws InterruptedException if interrupted while stubbing.
     */
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
        @DisplayName("INET")
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
        @DisplayName("INET6")
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
        @DisplayName("UNIX")
        @Disabled("AsynchronousServerSocketChannel does not support UNIX domain")
        @Test
        void __UNIX() {
            // AsynchronousServerSocketChannel has no open(ProtocolFamily) overload
        }
    }

    /**
     * Drives an <a href="https://www.rfc-editor.org/rfc/rfc862">RFC&nbsp;862</a>-style asynchronous
     * echo server, sourced through
     * {@link HelloWorld#write(AsynchronousByteChannel) write(channel)}, end-to-end on the loopback
     * interface.
     * <p>
     * The server runs on an {@link AsynchronousServerSocketChannel} bound to
     * <em>{@code IPv6}-loopback unless {@code java.net.preferIPv4Stack=true}</em>, on the
     * RFC&nbsp;862-flavoured port {@value #PORT}. It accepts indefinitely via a recursive accept
     * handler; each accepted connection is echoed through a 1-byte-capacity, self-feeding
     * read/write {@link CompletionHandler} pair, so every byte round-trips through a one-character
     * buffer.
     * <p>
     * {@value #CLIENT_COUNT} virtual-thread clients connect in parallel. Each client calls
     * {@code service.write(channel)} {@value #ECHOES_PER_CLIENT} times (sending
     * {@value #ECHOES_PER_CLIENT}&nbsp;&times;&nbsp;{@value HelloWorld#BYTES} bytes total),
     * {@linkplain AsynchronousSocketChannel#shutdownOutput() half-closes} to signal end-of-input,
     * drains exactly {@value #ECHOES_PER_CLIENT}&nbsp;&times;&nbsp;{@value HelloWorld#BYTES} bytes
     * back from the server, and asserts that each {@value HelloWorld#BYTES}-byte chunk decodes to
     * {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}.
     */
    @DisplayName("RFC 862 echo")
    @Nested
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    class Rfc862_Test {

        /**
         * RFC 862 assigns echo to port 7; this test uses {@value} (the privileged-port-free echo
         * alias commonly used in user-space echo demos) to avoid root requirements while staying
         * close to the spirit of the spec.
         */
        private static final int PORT = 50007;

        /**
         * Number of concurrent clients driven against the echo server.
         */
        private static final int CLIENT_COUNT = 4;

        /**
         * Number of {@code hello, world} messages each client writes (and reads back) per
         * connection.
         */
        private static final int ECHOES_PER_CLIENT = 8;

        /**
         * Number of bytes each client exchanges in total (in either direction).
         */
        private static final int PAYLOAD_BYTES = ECHOES_PER_CLIENT * HelloWorld.BYTES;

        /**
         * Wires a 1-byte-capacity self-echo pipeline on the given channel — reads one byte, writes
         * it back, repeats until upstream EOF, then closes the channel. Failures close the channel
         * quietly.
         *
         * @param client the accepted asynchronous socket channel.
         */
        private static void echo(final AsynchronousSocketChannel client) {
            final var buf = ByteBuffer.allocate(1);
            final var reader = new AtomicReference<CompletionHandler<Integer, Object>>();
            final var writer = new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(final Integer w, final Object a) {
                    if (buf.hasRemaining()) {
                        client.write(buf, null, this);
                        return;
                    }
                    client.read(buf.clear(), null, reader.get());
                }

                @Override
                public void failed(final Throwable t, final Object a) {
                    closeQuietly(client, t);
                }
            };
            reader.set(new CompletionHandler<>() {
                @Override
                public void completed(final Integer r, final Object a) {
                    if (r == -1) {
                        closeQuietly(client, null);
                        return;
                    }
                    client.write(buf.flip(), null, writer);
                }

                @Override
                public void failed(final Throwable t, final Object a) {
                    closeQuietly(client, t);
                }
            });
            client.read(buf, null, reader.get());
        }

        private static void closeQuietly(final AsynchronousSocketChannel c, final Throwable t) {
            if (t != null && !(t instanceof AsynchronousCloseException)) {
                log.error("[server] echo failed; closing {}", c, t);
            }
            try {
                c.close();
            } catch (final IOException ignored) {
            }
        }

        /**
         * Verifies that the method writes {@code hello-world-bytes} through an asynchronous echo
         * server when driven concurrently by {@value #CLIENT_COUNT} clients, each sending
         * {@value #ECHOES_PER_CLIENT} copies of
         * {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} and reading the echoed bytes back
         * intact.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("happy path")
        @Test
        void __() throws Exception {
            // ----------------------------------------------------------------------------- address
            final var preferIpv4 = Boolean.getBoolean("java.net.preferIPv4Stack");
            final var host = preferIpv4 ? "127.0.0.1" : "::1";
            final var bindAddress = new InetSocketAddress(InetAddress.getByName(host), PORT);
            // ------------------------------------------------------------------------------ server
            try (final var server = AsynchronousServerSocketChannel.open()) {
                server.bind(bindAddress);
                log.debug("[server] bound to {}", server.getLocalAddress());
                final var accept = new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    @Override
                    public void completed(final AsynchronousSocketChannel client, final Object a) {
                        server.accept(null, this);
                        try {
                            log.debug("[server] accepted from {}", client.getRemoteAddress());
                        } catch (final IOException ignored) {
                            // accepted-channel address lookup is best-effort logging only
                        }
                        echo(client);
                    }

                    @Override
                    public void failed(final Throwable t, final Object a) {
                        if (!(t instanceof AsynchronousCloseException)) {
                            log.error("[server] accept failed", t);
                        }
                    }
                };
                server.accept(null, accept);
                // -------------------------------------------------------------------------- clients
                final var serverAddress = server.getLocalAddress();
                final var tasks = new ArrayList<Callable<Void>>(CLIENT_COUNT);
                for (var i = 0; i < CLIENT_COUNT; i++) {
                    final var id = i;
                    tasks.add(() -> {
                        try (final var client = AsynchronousSocketChannel.open()) {
                            client.connect(serverAddress).get();
                            log.debug("[client-{}] connected to {}", id, client.getRemoteAddress());
                            for (var m = 0; m < ECHOES_PER_CLIENT; m++) {
                                service().write(client);
                            }
                            client.shutdownOutput();
                            log.debug("[client-{}] sent {} bytes", id, PAYLOAD_BYTES);
                            final var dst = ByteBuffer.allocate(PAYLOAD_BYTES);
                            while (dst.hasRemaining()) {
                                if (client.read(dst).get() == -1) {
                                    break;
                                }
                            }
                            assertFalse(dst.hasRemaining(),
                                        "client-" + id + " short-read: " + dst);
                            dst.flip();
                            for (var m = 0; m < ECHOES_PER_CLIENT; m++) {
                                final var chunk = new byte[HelloWorld.BYTES];
                                dst.get(chunk);
                                assertEquals(HELLO_WORLD_STRING, new String(chunk, US_ASCII),
                                             "client-" + id + " mismatch at chunk " + m);
                            }
                            log.debug("[client-{}] received and verified", id);
                        }
                        return null;
                    });
                }
                try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    for (final var future : executor.invokeAll(tasks)) {
                        future.get();
                    }
                }
            }
        }
    }
}
