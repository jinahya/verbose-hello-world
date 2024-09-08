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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

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
 * handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, handler, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_06_Write_AsynchronousByteChannelWithHandler_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * handler, attachment)} method throws a {@link NullPointerException} when the {@code channel}
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
     * handler, attachment)} method throws a {@link NullPointerException} when the {@code handler}
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
     * handler, attachment)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method
     * with a byte buffer of {@value HelloWorld#BYTES} bytes, continuously invokes
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * channel.write(buffer, attachment, a-handler)}, and eventually invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(Object, Object)
     * handler.completed(channel, attachment)}.
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
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var writtenSoFar = new LongAdder();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // channel.write(src, attachment, handler) will drain the src
        final var threadRef = new AtomicReference<Thread>();
        BDDMockito.willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            Thread.ofVirtual().start(() -> {
                final var previous = threadRef.get();
                if (previous != null) {
                    try {
                        previous.join();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ie);
                    }
                }
                threadRef.set(Thread.currentThread());
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
        final var bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor.capture());
        final var buffer = bufferCaptor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // TODO: verify, handler.completed(channel, attachment) invoked, once, within some time.
        // TODO: verify, channel.write(buffer, attachment, a-handler) invoked, at least once.
        // TODO: assert, writtenSoFar.intValue() is equal to BYTES
        // TODO: assert, buffer ha no remaining
    }

    @Disabled("not implemented yet")
    @畵蛇添足
    @Test
    void __AsynchronousSocketChannel() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // start a server thread which
        //           binds on a random port,
        //           accepts a client,
        //           and reads 12 bytes
        final var addr = InetAddress.getLoopbackAddress();
        final var port = new ArrayBlockingQueue<Integer>(1);
        Thread.ofPlatform().name("server").start(() -> {
            try (var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(addr, 0), 1);
                final var localAddress = server.getLocalAddress();
                log.debug("listening on {}", localAddress);
                port.put(((InetSocketAddress) localAddress).getPort());
                final var latch = new CountDownLatch(1);
                server.accept(null, new CompletionHandler<>() { // @formatter:off
                    @Override public void completed(final AsynchronousSocketChannel client,
                                                    final Object attachment) {
                        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                        client.read(buffer, null, new CompletionHandler<>() {
                            @Override public void completed(final Integer written,
                                                            final Object attachment) {
                                if (!buffer.hasRemaining()) {
                                    log.debug("read");
                                    latch.countDown();
                                    return;
                                }
                                client.write(buffer, null, this);
                            }
                            @Override public void failed(final Throwable exc,
                                                         final Object attachment) {
                                log.error("failed to read", exc);
                                latch.countDown();
                            }});
                    }
                    @Override public void failed(final Throwable exc, final Object attachment) {
                        log.error("failed to accept", exc);
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
        //       send 12 bytes to the server
        Thread.currentThread().setName("client");
        try (var client = AsynchronousSocketChannel.open()) {
            final var remote = new InetSocketAddress(addr, port.take());
            log.debug("connecting to {}", remote);
            final var latch = new CountDownLatch(1);
            client.connect(remote, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Void result, final Object attachment) {
                    log.debug("connected");
                    log.debug("writing...");
                    service.write(client, null, new CompletionHandler<>() {
                        @Override public void completed(final AsynchronousSocketChannel result,
                                                        final Object attachment) {
                            log.debug("written");
                            latch.countDown();
                        }
                        @Override public void failed(final Throwable exc, final Object attachment) {
                            throw new RuntimeException("failed to write", exc);
                        }
                    });
                }
                @Override public void failed(final Throwable exc, final Object attachment) {
                    throw new RuntimeException("failed to connect", exc);
                } // @formatter:on
            });
            final var broken = latch.await(1L, TimeUnit.SECONDS);
            Assertions.assertTrue(broken, "not broken");
        }
    }
    // ---------------------------------------------------------------------------------------- then
    // empty
}
