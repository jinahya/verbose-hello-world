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
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Util_Concurrent_ExecutorService_TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
 * write(channel, attachment, handler)} method with real implementations.
 *
 * @param <T> the subtype of {@link AsynchronousHelloWorld}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("AsynchronousHelloWorld.write(AsynchronousByteChannel, attachment, handler) / extras")
@Slf4j
abstract class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler__Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    // ---------------------------------------------------------------------------------------------
    AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler__Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs {@code asynchronousService.write(channel, attachment, handler)} to write the
     * {@code hello-world-bytes} to the channel before each test.
     */
    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __stubService() {
        doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            final var src = hello_world_byte_buffer();
            channel.write(src, attachment, new CompletionHandler<>() { // @formatter:off
                @Override
                public void completed(final Integer result, final Object attachment) {
                    if (src.hasRemaining()) {
                        channel.write(src, attachment, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }
                @Override
                public void failed(final Throwable exc, final Object attachment) {
                    handler.failed(exc, attachment);
                } // @formatter:on
            });
            return null;
        }).when(asynchronousService()).write(any(), any(), any());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("echo server")
    @Nested
    class EchoServer_Test {

        private static Stream<Named<ExecutorService>> executorStream() {
            return executorStreamAsynchronousChannelGroup();
        }

        /**
         * Verifies that the method writes {@code hello-world-bytes} to an echo server with a real
         * {@link AsynchronousChannelGroup}.
         *
         * @param executor the executor to back the channel group.
         * @throws Exception if an error occurs.
         */
        @DisplayName("echo server / AsynchronousChannelGroup")
        @MethodSource({"executorStream"})
        @ParameterizedTest
        void __(final ExecutorService executor) throws Exception { // @formatter:off
            final var group = AsynchronousChannelGroup.withThreadPool(executor);
            try (var server = AsynchronousServerSocketChannel.open(group)) {
                server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                server.accept(null, new CompletionHandler<>() {
                    @Override
                    public void completed(final AsynchronousSocketChannel r1, final Object a1) {
                        log.info("[server] accepted");
                        try (r1) {
                            final var future = new CompletableFuture<Void>();
                            final var buf = ByteBuffer.allocate(HelloWorld.BYTES);
                            r1.read(buf, null, new CompletionHandler<>() {
                                @Override
                                public void completed(final Integer r2, final Object a2) {
                                    if (r2 == -1) {
                                        future.completeExceptionally(new EOFException());
                                        return;
                                    }
                                    if (buf.hasRemaining()) {
                                        r1.read(buf, null, this);
                                        return;
                                    }
                                    log.debug("[server] completed; read[12]");
                                    buf.flip();
                                    r1.write(buf, null, new CompletionHandler<>() {
                                        @Override
                                        public void completed(final Integer r3, final Object a3) {
                                            if (buf.hasRemaining()) {
                                                r1.write(buf, null, this);
                                                return;
                                            }
                                            log.debug("[server] completed; write[12]");
                                            future.complete(null);
                                        }
                                        @Override
                                        public void failed(final Throwable t3, final Object a3) {
                                            future.completeExceptionally(t3);
                                        }
                                    });
                                }
                                @Override
                                public void failed(final Throwable t2, final Object a2) {
                                    future.completeExceptionally(t2);
                                }
                            });
                            future.get(8L, TimeUnit.SECONDS);
                        } catch (final Exception _) {
                        }
                    }
                    @Override
                    public void failed(final Throwable t1, final Object a1) {
                    }
                });
                // ---------------------------------------------------------------------------------
                try (var client = AsynchronousSocketChannel.open(group)) {
                    final var future = new CompletableFuture<Void>();
                    client.connect(server.getLocalAddress(), null, new CompletionHandler<>() {
                        @Override
                        public void completed(final Void r1, final Object a1) {
                            log.info("[client] connected");
                            asynchronousService().write(client, null, new CompletionHandler<>() {
                                @Override
                                public void completed(final AsynchronousSocketChannel r2,
                                                      final Object a2) {
                                    log.debug("[client] completed; write[12]");
                                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                                    client.read(dst, null, new CompletionHandler<>() {
                                        @Override
                                        public void completed(Integer n, Object a3) {
                                            if (n == -1) {
                                                future.completeExceptionally(new EOFException());
                                                return;
                                            }
                                            if (dst.hasRemaining()) {
                                                client.read(dst, null, this);
                                                return;
                                            }
                                            log.debug("[client] completed; read[12]");
                                            future.complete(null);
                                        }
                                        @Override
                                        public void failed(final Throwable t3, final Object a3) {
                                            future.completeExceptionally(t3);
                                        }
                                    });
                                }
                                @Override
                                public void failed(final Throwable t2, final Object a2) {
                                    future.completeExceptionally(t2);
                                }
                            });
                        }
                        @Override
                        public void failed(final Throwable t1, final Object a1) {
                            future.completeExceptionally(t1);
                        }
                    });
                    future.get(8L, TimeUnit.SECONDS);
                }
            } finally {
                group.shutdown();
                if (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                    log.warn("channel group did not terminate within 8s");
                }
            } // @formatter:on
        }
    }
}
