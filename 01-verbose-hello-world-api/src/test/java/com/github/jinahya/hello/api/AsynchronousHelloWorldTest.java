package com.github.jinahya.hello.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An abstract class for testing a specific subclass of {@link AsynchronousHelloWorldTest}.
 *
 * @param <T> subclass type parameter.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class AsynchronousHelloWorldTest<T extends AsynchronousHelloWorld> {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified type.
     *
     * @param type the type to test
     */
    AsynchronousHelloWorldTest(final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }

    @Test
    void test() {
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("java.nio.channel.AsynchornousByteChnanel")
    @Nested
    class AsynchronousByteChannelTest {

        @DisplayName("throws NullPointerException if channel is null")
        @Test
        void _ThrowNullPointerException_ChannelIsNull() {
            // ------------------------------------------------------------------------------- given
            final var instance = getTypeInstance();
            final AsynchronousByteChannel channel = null;
            final var attachment = new Object();
            final var handler = Mockito.mock(CompletionHandler.class);
            // ------------------------------------------------------------------------- when / then
            Assertions.assertThrows(NullPointerException.class, () -> {
                instance.write(channel, attachment, handler);
            });
        }

        @DisplayName("does not throw NullPointerException if attachment is null")
        @Test
        void _DoesnotThrow_AttachmentIsNull() {
            // -------------------------------------------------------------------------------- when
            final var instance = getTypeInstance();
            final var channel = Mockito.mock(AsynchronousByteChannel.class);
            final Object attachment = null;
            final var handler = Mockito.mock(CompletionHandler.class);
            // ------------------------------------------------------------------------- when / then
            Assertions.assertDoesNotThrow(() -> {
                instance.write(channel, attachment, handler);
            });
        }

        @DisplayName("throws NullPointerException if handler is null")
        @Test
        void _ThrowNullPointerException_HandlerIsNull() {
            // -------------------------------------------------------------------------------- when
            final var instance = getTypeInstance();
            final var channel = Mockito.mock(AsynchronousByteChannel.class);
            final var attachment = new Object();
            final CompletionHandler<AsynchronousByteChannel, Object> handler = null;
            // ------------------------------------------------------------------------- when / then
            // TODO:
            //  assert
            //  instance.write(channel, attachment, handler)
            //  throws NullPointerException
        }

        @DisplayName("should invoke completed(channel, attachment)")
        @Test
        void _InvokeCompleted_() {
            // ------------------------------------------------------------------------------- given
            final var instance = getTypeInstance();
            final var channel = Mockito.mock(AsynchronousByteChannel.class);
            final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
            final var handler = Mockito.mock(CompletionHandler.class);
            Mockito.doAnswer(i -> {
                        final var s = i.getArgument(0, ByteBuffer.class);
                        final var a = i.getArgument(1, Object.class);
                        final var h = i.getArgument(2, CompletionHandler.class);
                        Assertions.assertNotNull(s);
                        Assertions.assertTrue(s.hasRemaining());
                        Assertions.assertNotNull(h);
                        final var r = ThreadLocalRandom.current().nextInt(s.remaining()) + 1;
                        s.position(s.position() + r);
                        h.completed(r, a);
                        return null;
                    })
                    .when(channel)
                    .write(Mockito.any(), Mockito.any(), Mockito.any());
            // -------------------------------------------------------------------------------- when
            instance.write(channel, attachment, handler);
            // -------------------------------------------------------------------------------- then
            final var inOrder = Mockito.inOrder(handler);
            inOrder.verify(handler).completed(
                    channel,
                    attachment
            );
            inOrder.verifyNoMoreInteractions();
        }

        @DisplayName("should invoke failed(thrown, attachment)")
        @Test
        void _InvokeFailed_() {
            // ------------------------------------------------------------------------------- given
            final var instance = getTypeInstance();
            final var channel = Mockito.mock(AsynchronousByteChannel.class);
            final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
            final var handler = Mockito.mock(CompletionHandler.class);
            final var thrown = new Throwable();
            Mockito.doAnswer(i -> {
                        final var a = i.getArgument(1, Object.class);
                        final var h = i.getArgument(2, CompletionHandler.class);
                        h.failed(thrown, a);
                        return null;
                    })
                    .when(channel)
                    .write(Mockito.any(), Mockito.any(), Mockito.any());
            // -------------------------------------------------------------------------------- when
            instance.write(channel, attachment, handler);
            // -------------------------------------------------------------------------------- then
            final var inOrder = Mockito.inOrder(handler);
            inOrder.verify(handler).failed(
                    ArgumentMatchers.same(thrown),
                    ArgumentMatchers.same(attachment)
            );
            inOrder.verifyNoMoreInteractions();
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("java.nio.channel.AsynchornousSocketChnanel")
    @Nested
    class AsynchronousSocketChannelTest {

        @Test
        void __() throws IOException, InterruptedException {
            final var instance = getTypeInstance();
            // -------------------------------------------------------------------------------------
            final var server = new ServerSocket();
            // start the server accepts and reads 12 bytes
            Thread.ofPlatform().daemon().start(() -> {
                try (server) {
                    server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                    logger.log(System.Logger.Level.DEBUG, "bound to {0}",
                               server.getLocalSocketAddress());
                    synchronized (server) {
                        server.notify();
                    }
                    try (var accepted = server.accept()) {
                        final var array = new byte[HelloWorld.BYTES];
                        final var r = accepted.getInputStream().readNBytes(array, 0, array.length);
                        assert r == array.length;
                        logger.log(System.Logger.Level.INFO, "read: {0}",
                                   new String(array, StandardCharsets.US_ASCII));
                        synchronized (server) {
                            server.notify();
                        }
                    }
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                synchronized (server) {
                    server.notify();
                }
            });
            // -------------------------------------------------------------------------------------
            // connect to the server, and send bytes
            try (var client = AsynchronousSocketChannel.open()) {
                synchronized (server) {
                    server.wait();
                }
                final var remote = server.getLocalSocketAddress();
                logger.log(System.Logger.Level.DEBUG, "connecting to {0}", remote);
                client.connect( // @formatter:off
                        remote,
                        null,
                        new CompletionHandler<>() {
                            @Override
                            public void completed(final Void result, final Object attachment) {
                                instance.send(
                                        client,
                                        null,
                                        new CompletionHandler<>() {
                                            @Override
                                            public void completed(
                                                    final AsynchronousSocketChannel result,
                                                    final Object attachment) {
                                            }

                                            @Override
                                            public void failed(final Throwable exc,
                                                               final Object attachment) {
                                                logger.log(System.Logger.Level.ERROR,
                                                           "failed to send", exc);
                                            }
                                        }
                                );
                            }
                            @Override
                            public void failed(final Throwable exc, final Object attachment) {
                                logger.log(System.Logger.Level.ERROR, "failed to connect", exc);
                            }
                        }
                ); // @formatter:on
                synchronized (server) {
                    server.wait();
                }
            }
            // -------------------------------------------------------------------------------------
            // wait for the server to be closed
            synchronized (server) {
                server.wait();
            }
            assert server.isClosed();
        }
    }

    // ---------------------------------------------------------------------------------------- type

    /**
     * Returns a new instance of {@link #type}.
     *
     * @return a new instance of {@link #type}.
     */
    T newTypeInstance() {
        return ReflectionUtils.newInstance(type);
    }

    // ------------------------------------------------------------------------------------ instance

    /**
     * Returns an instance of {@link #type}.
     *
     * @return an instance of {@link #type}.
     */
    final T getTypeInstance() {
        T result = _instance;
        if (result == null) {
            result = _instance = newTypeInstance();
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    final Class<T> type;

    private volatile T _instance;
}
