package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.DefaultAsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment, handler)")
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronouseByteChannel_Test
        extends DefaultAsynchronousHelloWorldTest {

    /**
     * Verifies that the
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
     * write(channel, attachment, handler)} method throws a {@link NullPointerException} when the
     * {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>""")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousByteChannel) null;
        final var attachment = (Void) null;
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
     * write(channel, attachment, handler)} method throws a {@link NullPointerException} when the
     * {@code handler} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <handler> argument is <null>""")
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var attachment = (Void) null;
        final var handler = (CompletionHandler<AsynchronousByteChannel, Void>) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
     * write(channel, attachment, handler)} method writes all {@value HelloWorld#BYTES} bytes to the
     * channel and invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            should write all <hello-world-bytes> to the <channel>,
            and invoke <handler.completed(channel, attachment)>""")
    @Test
    @SuppressWarnings({"unchecked"})
    void _completed_() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // a mock object of <AsynchronousByteChannel>
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // the total number of bytes written to the <channel>
        final var written = new LongAdder();
        // stub, <channel.write(src, attachment, handler)> will start a new thread
        //         which increases <src>'s <position> by a random value.
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            // start a new thread which increases <src>'s <position> by a random value.
            Thread.ofPlatform().start(() -> {
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                written.add(result);
                handler.completed(result, attachment);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.notNull(), // <src>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        // an attachment; <null> or non-<null>
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // a mock object of <CompletionHandler>
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <handler.completed(channel, attachment)> invoked, once, within some time.
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        // verify, <channel.write(buffer, ?, handler)> invoked, at least once,
        //         and the same <buffer> and <handler> instances are reused across all invocations.
        final var srcCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        final var handlerCaptor = ArgumentCaptor.forClass(CompletionHandler.class);
        Mockito.verify(channel, Mockito.atLeastOnce()).write(
                srcCaptor.capture(),    // <src>
                ArgumentMatchers.any(), // <attachment>
                handlerCaptor.capture() // <handler>
        );
        final var srcs = srcCaptor.getAllValues();
        srcs.forEach(s -> Assertions.assertSame(srcs.getFirst(), s));
        final var handlers = handlerCaptor.getAllValues();
        handlers.forEach(h -> Assertions.assertSame(handlers.getFirst(), h));
        // assert, <written.sum()> is equal to <HelloWorld.BYTES>
        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
    }

    /**
     * Verifies that the
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)
     * write(channel, attachment, handler)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} when the
     * {@code channel} fails to write.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when the <channel> fails to write""")
    @Test
    @SuppressWarnings({"unchecked"})
    void _failed_() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // a mock object of <AsynchronousByteChannel>
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // the exception to be thrown by the <channel>
        final var exc = new RuntimeException("simulated write failure");
        // stub, <channel.write(src, attachment, handler)> will start a new thread
        //         which invokes <handler.failed(exc, attachment)>.
        Mockito.doAnswer(i -> {
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                handler.failed(exc, attachment);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.notNull(), // <src>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        // an attachment; <null> or non-<null>
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // a mock object of <CompletionHandler>
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <handler.failed(exc, attachment)> invoked, once, within some time.
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        // verify, <handler.completed(?, ?)> never invoked.
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> through a real
     * {@link AsynchronousSocketChannel} connected to an {@link AsynchronousServerSocketChannel},
     * and reads them back on the server side.
     *
     * @throws Exception if an error occurs.
     */
    @畵蛇添足
    @Test
    void _添足_畵蛇()
            throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var latch = new CountDownLatch(1);
        // ---------------------------------------------------------------------------------  server
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 1);
            log.debug("bound to {}", server.getLocalAddress());
            server.accept(null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final AsynchronousSocketChannel client,
                                                final Object a) {
                    log.debug("accepted: {}", client);
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    client.read(buffer, null, new CompletionHandler<>() {
                        @Override public void completed(final Integer result, final Object a) {
                            log.debug("read: {}", result);
                            if (result == -1) {
                                throw new RuntimeException("eof");
                            }
                            if (!buffer.hasRemaining()) {
                                log.debug("\tdecoded: {}",
                                          StandardCharsets.US_ASCII.decode(buffer.flip()));
                                latch.countDown();
                                return;
                            }
                            client.read(buffer, null, this);
                        }
                        @Override public void failed(final Throwable exc, final Object a) {
                            log.error("failed to read", exc);
                            latch.countDown();
                        }
                    });
                }
                @Override public void failed(final Throwable exc, final Object a) {
                    log.error("failed to accept", exc);
                    latch.countDown();
                } // @formatter:on
            });
            // -----------------------------------------------------------------------------  client
            try (var client = AsynchronousSocketChannel.open()) {
                final var remote = server.getLocalAddress();
                client.connect(remote, null, new CompletionHandler<>() { // @formatter:off
                    @Override public void completed(final Void r, final Object a) {
                        log.debug("connected");
                        service.write(client, null, new CompletionHandler<>() {
                            @Override public void completed(final AsynchronousSocketChannel c,
                                                            final Object a) {
                                log.debug("written");
                                Assertions.assertSame(client, c);
                            }
                            @Override public void failed(final Throwable exc, final Object a) {
                                log.error("failed to write", exc);
                            }
                        });
                    }
                    @Override public void failed(final Throwable exc, final Object a) {
                        log.error("failed to connect", exc);
                    } // @formatter:on
                });
                // await the server to read all bytes before closing the <client>
                Assertions.assertTrue(
                        latch.await(8L, TimeUnit.SECONDS),
                        "server read did not complete in time"
                );
            }
        }
    }
}
