package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler__Test
        extends AsynchronousHelloWorldTest {

    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __() {
        // stub <asynchronousService.write(channel, attachment, handler)> directly:
        // drain the hello-world bytes onto the <channel> using the channel's
        // <write(buffer, attachment, handler)> overload — each completion recursively re-invokes
        // the channel's write until the buffer has no remaining; finally notify
        // <handler.completed(channel, attachment)>, or <handler.failed(exc, attachment)> on error.
        final AsynchronousHelloWorld service = asynchronousService();
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            final var buffer = HelloWorldTestUtils.hello_world_byte_buffer();
            channel.write(buffer, attachment, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(final Integer result, final Object a) {
                    if (buffer.hasRemaining()) {
                        channel.write(buffer, a, this);
                        return;
                    }
                    handler.completed(channel, a);
                }

                @Override
                public void failed(final Throwable exc, final Object a) {
                    handler.failed(exc, a);
                }
            });
            return null;
        }).when(service).write(
                ArgumentMatchers.<AsynchronousByteChannel>notNull(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<CompletionHandler<AsynchronousByteChannel, Object>>notNull()
        );
    }

    private static void closeQuietly(final AsynchronousByteChannel channel) {
        if (channel == null) {
            return;
        }
        try {
            channel.close();
        } catch (final IOException _) {
            // unnamed pattern — nothing to do on close failure in tests
        }
    }

    /**
     * Tests
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
     * write(channel, attachment, handler)} with the channel obtained from
     * {@link AsynchronousServerSocketChannel#accept(Object, CompletionHandler) server.accept(att,
     * handler)}.
     */
    @DisplayName("write(<accept>(att, handler), attachment, handler)")
    @Nested
    class AsynchronousServerSocketChannelTest {

        @Test
        void __() throws IOException, InterruptedException {
            try (var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                final var latch = new CountDownLatch(2);
                final var acceptedRef = new AtomicReference<AsynchronousSocketChannel>();
                server.accept(null, new CompletionHandler<>() {
                    @Override
                    public void completed(final AsynchronousSocketChannel accepted,
                                          final Object a) {
                        acceptedRef.set(accepted);
                        final AsynchronousHelloWorld service = asynchronousService();
                        service.write(accepted, null, new CompletionHandler<>() {
                            @Override
                            public void completed(final AsynchronousSocketChannel result,
                                                  final Object a2) {
                                latch.countDown();
                            }

                            @Override
                            public void failed(final Throwable exc, final Object a2) {
                                log.error("failed to write", exc);
                                latch.countDown();
                            }
                        });
                    }

                    @Override
                    public void failed(final Throwable exc, final Object a) {
                        log.error("failed to accept", exc);
                        latch.countDown();
                    }
                });
                try (var client = AsynchronousSocketChannel.open()) {
                    client.connect(
                            server.getLocalAddress(),
                            null,
                            new CompletionHandler<>() {
                                @Override
                                public void completed(final Void result, final Object a) {
                                    final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                                    client.read(dst, dst, new CompletionHandler<>() {
                                        @Override
                                        public void completed(final Integer n,
                                                              final ByteBuffer d) {
                                            if (d.hasRemaining()) {
                                                client.read(d, d, this); // recursive
                                                return;
                                            }
                                            log.debug("received: {}",
                                                      StandardCharsets.US_ASCII
                                                              .decode(d.flip()));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void failed(final Throwable exc,
                                                           final ByteBuffer d) {
                                            log.error("failed to read", exc);
                                            latch.countDown();
                                        }
                                    });
                                }

                                @Override
                                public void failed(final Throwable exc, final Object a) {
                                    log.error("failed to connect", exc);
                                    latch.countDown();
                                }
                            }
                    );
                    try {
                        Assertions.assertTrue(latch.await(8L, TimeUnit.SECONDS));
                    } finally {
                        closeQuietly(acceptedRef.get());
                    }
                }
            }
        }
    }

    /**
     * Tests
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
     * write(channel, attachment, handler)} with an {@link AsynchronousSocketChannel} on the client
     * side.
     */
    @DisplayName("write(<connect>(att, handler), attachment, handler)")
    @Nested
    class AsynchronousSocketChannelTest {

        @Test
        void __() throws IOException, InterruptedException {
            try (var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                final var latch = new CountDownLatch(2);
                final var acceptedRef = new AtomicReference<AsynchronousSocketChannel>();
                // ----------- server: <accept>(att, handler) → <read>(buf, att, handler)
                server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    @Override
                    public void completed(final AsynchronousSocketChannel accepted,
                                          final Object a) {
                        acceptedRef.set(accepted);
                        final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                        accepted.read(dst, dst, new CompletionHandler<>() {
                            @Override
                            public void completed(final Integer n, final ByteBuffer d) {
                                if (d.hasRemaining()) {
                                    accepted.read(d, d, this); // recursive
                                    return;
                                }
                                log.debug("received: {}",
                                          StandardCharsets.US_ASCII.decode(d.flip()));
                                latch.countDown();
                            }

                            @Override
                            public void failed(final Throwable exc, final ByteBuffer d) {
                                log.error("failed to read", exc);
                                latch.countDown();
                            }
                        });
                    }

                    @Override
                    public void failed(final Throwable exc, final Object a) {
                        log.error("failed to accept", exc);
                        latch.countDown();
                    }
                });
                // ----------- client: <connect>(addr, att, handler) → service.write(client, ...)
                try (var client = AsynchronousSocketChannel.open()) {
                    client.connect(server.getLocalAddress(), null,
                                   new CompletionHandler<Void, Object>() {
                                       @Override
                                       public void completed(final Void result, final Object a) {
                                           final AsynchronousHelloWorld service =
                                                   asynchronousService();
                                           service.write(client, null,
                                                         new CompletionHandler<>() {
                                                             @Override
                                                             public void completed(
                                                                     final AsynchronousSocketChannel result2,
                                                                     final Object a2) {
                                                                 latch.countDown();
                                                             }

                                                             @Override
                                                             public void failed(
                                                                     final Throwable exc,
                                                                     final Object a2) {
                                                                 log.error("failed to write", exc);
                                                                 latch.countDown();
                                                             }
                                                         });
                                       }

                                       @Override
                                       public void failed(final Throwable exc, final Object a) {
                                           log.error("failed to connect", exc);
                                           latch.countDown();
                                       }
                                   });
                    try {
                        Assertions.assertTrue(latch.await(8L, TimeUnit.SECONDS));
                    } finally {
                        closeQuietly(acceptedRef.get());
                    }
                }
            }
        }
    }
}
