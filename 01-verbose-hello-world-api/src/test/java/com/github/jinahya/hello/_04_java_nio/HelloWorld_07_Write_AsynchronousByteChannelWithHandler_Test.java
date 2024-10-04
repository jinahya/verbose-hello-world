package com.github.jinahya.hello._04_java_nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldTest;
import com.github.jinahya.hello.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
 * attachment, handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment, handler)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_07_Write_AsynchronousByteChannelWithHandler_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <channel> argument is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousByteChannel) null;
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} method throws a {@link NullPointerException} when the {@code handler}
     * argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var handler = (CompletionHandler<AsynchronousByteChannel, Void>) null;
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method
     * with a byte buffer of {@value HelloWorld#BYTES} bytes, continuously invokes
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * channel.write(buffer, attachment, a-handler)} method while the {@code buffer} has remaining,
     * and eventually invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            should invoke put(buffer[12])
            and write the <buffer> to the <channel> while the <buffer> has remaining"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase <buffer>'s <position> by <12>
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var writtenSoFar = new LongAdder();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // stub, <channel.write(src, attachment, handler)> will drain the <src>
        final var reference = new AtomicReference<Thread>();
        BDDMockito.willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            Thread.ofVirtual().start(() -> {
                final var previous = reference.get();
                if (previous != null) {
                    try {
                        previous.join();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("interrupted while joining previous thread", ie);
                    }
                }
                reference.set(Thread.currentThread());
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                handler.completed(result, attachment);
                writtenSoFar.add(result);
            });
            return null;
        }).given(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.put(buffer[12])> invoked, once
        final var bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor.capture());
        final var buffer = bufferCaptor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // verify, <handler.completed(channel, attachment)> invoked, once, within some time.

        // verify, <channel.write(buffer, attachment, a-handler)> invoked, at least once.

        // assert, <writtenSoFar.intValue()> is equal to HelloWorld.BYTES>

        // assert, <buffer> ha no <remaining>
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel, attachment, handler)> will write 12 bytes
        BDDMockito.willAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            final var attachment = i.getArgument(1);
            @SuppressWarnings({"unchecked"})
            final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                    i.getArgument(2, CompletionHandler.class);
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            channel.write(buffer, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Integer result, final Object a) {
                    if (buffer.hasRemaining()) {
                        channel.write(buffer, null, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }
                @Override public void failed(final Throwable exc, final Object a) {
                    log.error("failed to write", exc);
                    handler.failed(exc, attachment);
                }
            }); // @formatter:on
            return null;
        }).given(service).write(
                ArgumentMatchers.notNull(), // <channel>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        // start a server thread which
        //         binds on a random port,
        //         accepts a client,
        //         and reads 12 bytes
        final var addr = InetAddress.getLoopbackAddress();
        final var port = new ArrayBlockingQueue<Integer>(1);
        Thread.ofPlatform().name("server").start(() -> {
            try (var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(addr, 0), 1);
                final var address = server.getLocalAddress();
                log.debug("listening on {}", address);
                port.put(((InetSocketAddress) address).getPort());
                final var latch = new CountDownLatch(1);
                server.accept(null, new CompletionHandler<>() { // @formatter:off
                    @Override public void completed(final AsynchronousSocketChannel c,
                                                    final Object a) {
                        try {
                            log.debug("accepted from {}", c.getRemoteAddress());
                        } catch (final IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                        c.read(buffer, null, new CompletionHandler<>() {
                            @Override public void completed(final Integer r, final Object a) {
                                if (!buffer.hasRemaining()) {
                                    latch.countDown();
                                    return;
                                }
                                c.write(buffer, null, this);
                            }
                            @Override public void failed(final Throwable t, final Object a) {
                                log.error("failed to read", t);
                                latch.countDown();
                            }});
                    }
                    @Override public void failed(final Throwable t, final Object a) {
                        log.error("failed to accept", t);
                        latch.countDown();
                    } // @formatter:on
                });
                latch.await();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
        // ------------------------------------------------------------------------------------ when
        // connect to the server
        //         send 12 bytes to the server
        Thread.currentThread().setName("client");
        try (var client = AsynchronousSocketChannel.open()) {
            final var remote = new InetSocketAddress(addr, port.take());
            log.debug("connecting to {}", remote);
            final var latch = new CountDownLatch(1);
            client.connect(remote, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Void r, final Object a) {
                    try {
                        log.debug("connected to {}", client.getRemoteAddress());
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                    service.write(client, null, new CompletionHandler<>() {
                        @Override public void completed(final AsynchronousSocketChannel c,
                                                        final Object a) {
                            latch.countDown();
                        }
                        @Override public void failed(final Throwable t, final Object a) {
                            log.error("failed to write", t);
                            latch.countDown();
                        }
                    });
                }
                @Override public void failed(final Throwable t, final Object a) {
                    log.error("failed to connect", t);
                    latch.countDown();
                } // @formatter:on
            });
            final var broken = latch.await(1L, TimeUnit.SECONDS);
            Assertions.assertTrue(broken, "not broken");
        }
    }
    // ---------------------------------------------------------------------------------------- then
    // empty
}
