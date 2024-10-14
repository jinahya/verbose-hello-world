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
import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import com.github.jinahya.hello.畵蛇添足;
import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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
            should throw a <NullPointerException>
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
        // assert, <service.write(channel, attachment, handler)> throws a <NullPointerException>
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
            should throw a <eNullPointerException>
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
        // assert, <service.write(channel, attachment, handler)> throws a <NullPointerException>
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
            should invoke <put(buffer[12])>,
            and write the <buffer> to the <channel> while the <buffer> has remaining"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase <buffer>'s <position> by <12>
        stub_put_buffer_will_increase_buffer_position_by_12();
        // total number of bytes written
        final var written = new LongAdder();
        // a mock object of <AsynchronousByteChannel>
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // stub, <channel.write(src, attachment, handler)> will drain the <src>
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                // increase the <src>'s position by a random (positive) value
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                JavaNioByteBufferUtils.print(src);
                handler.completed(result, attachment);
                written.add(result);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        // an attachment; <null> or non-<null>
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // a mock object of <CompletionHandler>
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.put(buffer[12])> invoked, once
        final var buffer = verify_put_buffer12_invoked_once();
        // verify, <handler.completed(channel, attachment)> invoked, once, within some time.
//        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(16L)).times(1))
//                .completed(channel, attachment);
        // verify, <channel.write(buffer, attachment, a-handler)> invoked, at least once.
//        final var captor = ArgumentCaptor.forClass(CompletionHandler.class);
//        Mockito.verify(channel, Mockito.atLeastOnce()).write(
//                ArgumentMatchers.same(buffer),
//                ArgumentMatchers.any(),
//                captor.capture()
//        );
//        final var handlers = captor.getAllValues();
//        Assertions.assertEquals(1, new HashSet<>(handlers).size());
        // assert, <buffer> ha no <remaining>
//        Assertions.assertFalse(buffer.hasRemaining());
        // assert, <written.sum()> is equal to <HelloWorld.BYTES>
//        Assertions.assertEquals(HelloWorld.BYTES, written.sum());
    }

    @DisplayName("write(AsynchronousSocketChannel)")
    @畵蛇添足
    @Test
    void _添足_畵蛇() throws Exception {
        // -----------------------------------------------------------------------------------------
        final var service = service();
        // stub, <service.write(channel, attachment, handler)> will write the <hello, world> bytes
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            final var attachment = i.getArgument(1);
            @SuppressWarnings({"unchecked"})
            final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                    i.getArgument(2, CompletionHandler.class);
            final var buffer = buffer();
            channel.write(buffer, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Integer result, final Object a) {
                    if (!buffer.hasRemaining()) {
                        handler.completed(channel, attachment);
                        return;
                    }
                    channel.write(buffer, null, this);
                }
                @Override public void failed(final Throwable exc, final Object a) {
                    log.error("failed to write", exc);
                    handler.failed(exc, attachment);
                }
            }); // @formatter:on
            return null;
        }).when(service).write(
                ArgumentMatchers.notNull(), // <channel>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        // -----------------------------------------------------------------------------------------
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 1);
            log.debug("bound to {}", server.getLocalAddress());
            final var latch = new CountDownLatch(1);
            // start a thread which
            //         accepts a client,
            //         and reads 12 bytes
            server.accept(null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final AsynchronousSocketChannel client,
                                                final Object a) {
                    try {
                        log.debug("accepted from {}", client.getRemoteAddress());
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    client.read(buffer, null, new CompletionHandler<>() {
                        @Override public void completed(final Integer r, final Object a) {
                            if (!buffer.hasRemaining()) {
                                log.debug("decoded: {}",
                                          StandardCharsets.US_ASCII.decode(buffer.flip()));
                                latch.countDown();
                                return;
                            }
                            client.write(buffer, null, this);
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
            // -------------------------------------------------------------------------------------
            // connect to the server
            //         send 12 bytes to the server
            final var remote = server.getLocalAddress();
            log.debug("connecting to {}", remote);
            final var client = AsynchronousSocketChannel.open(); // why no try-with-resources?
            final var semaphore = new Semaphore(1);
            semaphore.acquire();
            client.connect(remote, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Void r, final Object a) {
                    try {
                        log.debug("connected to {}", client.getRemoteAddress());
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                    log.debug("writing");
                    service.write(client, null, new CompletionHandler<>() {
                        @Override public void completed(final AsynchronousSocketChannel c,
                                                        final Object a) {
                            log.debug("written");
                            semaphore.release();
                        }
                        @Override public void failed(final Throwable t, final Object a) {
                            log.error("failed to write", t);
                            semaphore.release();
                        }
                    });
                }
                @Override public void failed(final Throwable t, final Object a) {
                    log.error("failed to connect", t);
                    semaphore.release();
                } // @formatter:on
            });
            // acquire the <semaphore>, and close the <client>
            semaphore.acquire();
            client.close();
            // await the <latch>, till <server-thread> finishes
            latch.await();
        } // try-with-<server>
    }
}
