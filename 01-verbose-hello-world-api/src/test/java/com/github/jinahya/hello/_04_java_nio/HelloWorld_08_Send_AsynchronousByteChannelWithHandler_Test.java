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
import com.github.jinahya.hello.屋上架屋;
import com.github.jinahya.hello.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#send(AsynchronousSocketChannel, Object, CompletionHandler)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
@Deprecated(forRemoval = true)
@DisplayName("send(AsynchronousSocketChannel, Object, CompletionHandler)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_08_Send_AsynchronousByteChannelWithHandler_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#send(AsynchronousSocketChannel, Object, CompletionHandler)} method throws a
     * {@link NullPointerException} when the {@code channel} argument is {@code null}.
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
        final var channel = (AsynchronousSocketChannel) null;
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.send(channel, attachment, handler)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#send(AsynchronousSocketChannel, Object, CompletionHandler)} method throws a
     * {@link NullPointerException} when the {@code handler} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <eNullPointerException>
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousSocketChannel.class);
        final var handler = (CompletionHandler<AsynchronousByteChannel, Void>) null;
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.send(channel, attachment, handler)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(channel, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#send(AsynchronousSocketChannel, Object, CompletionHandler)} method invokes
     * {@link HelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)} method with
     * given arguments.
     */
    @DisplayName("""
            should invoke <write(channel, attachment, handler)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel, attachment, handler> will do nothing
        Mockito.doNothing().when(service).write(
                ArgumentMatchers.notNull(), // <channel>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        final var channel = Mockito.mock(AsynchronousSocketChannel.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.write(channel, attachment, handler)> invoked, once
        Mockito.verify(service, Mockito.times(1)).write(channel, attachment, handler);
    }

    @DisplayName("write(AsynchronousSocketChannel)")
    @畵蛇添足("testing with a real socket doesn't add any value")
    @SuppressWarnings({"unchecked"})
    @Test
    void _添足_畵蛇() throws Exception {
        // -----------------------------------------------------------------------------------------
        final var service = service();
        // stub, <service.write(channel, attachment, handler)> will write the <hello, world> bytes
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            final var buffer = helloWorldBuffer();
            channel.write(buffer, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Integer result, final Object a) {
                    log.debug("channel.write.completed({}, {})", result, a);
                    if (!buffer.hasRemaining()) {
                        handler.completed(channel, attachment);
                        return;
                    }
                    channel.write(buffer, null, this);
                }
                @Override public void failed(final Throwable exc, final Object a) {
                    log.error("channel.write.failed({}, {})", exc, a, exc);
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
                    log.debug("server.accept.completed({}, {})", client, a);
                    try {
                        log.debug("\taccepted from {}", client.getRemoteAddress());
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    client.read(buffer, null, new CompletionHandler<>() {
                        @Override public void completed(final Integer result, final Object a) {
                            log.debug("accepted.read.completed({}, {})", result, a);
                            if (result == -1) {
                                throw new RuntimeException("eof");
                            }
                            assert result > 0; // why?
                            if (!buffer.hasRemaining()) {
                                log.debug("\tdecoded: {}",
                                          StandardCharsets.US_ASCII.decode(buffer.flip()));
                                latch.countDown();
                                return;
                            }
                            client.read(buffer, null, this);
                        }
                        @Override public void failed(final Throwable exc, final Object a) {
                            log.error("accepted.read.failed({}, {})", exc, a, exc);
                            latch.countDown();
                        }});
                }
                @Override public void failed(final Throwable exc, final Object a) {
                    log.error("server.accept.failed({}, {})", exc, a, exc);
                    latch.countDown();
                } // @formatter:on
            });
            // -------------------------------------------------------------------------------------
            // connect to the server
            //         send 12 bytes to the server
            try (final var client = AsynchronousSocketChannel.open()) {
                final var semaphore = new Semaphore(1);
                semaphore.acquire();
                final var remote = server.getLocalAddress();
                log.debug("connecting to {}", remote);
                client.connect(remote, null, new CompletionHandler<>() { // @formatter:off
                    @Override public void completed(final Void r, final Object a) {
                        log.debug("client.connect.completed({}, {})", r, a);
                        try {
                            log.debug("\tconnected to {}", client.getRemoteAddress());
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                        service.send(client, null, new CompletionHandler<>() {
                            @Override public void completed(final AsynchronousSocketChannel c,
                                                            final Object a) {
                                log.debug("service.write.completed({}, {})", c, a);
                                semaphore.release();
                            }
                            @Override public void failed(final Throwable exc, final Object a) {
                                log.error("service.write.failed({}, {})", exc, a, exc);
                                semaphore.release();
                            }
                        });
                    }
                    @Override public void failed(final Throwable exc, final Object a) {
                        log.error("client.connect.failed({}, {})", exc, a, exc);
                        semaphore.release();
                    } // @formatter:on
                });
                // acquire the <semaphore>, and close the <client>
                semaphore.acquire();
            } // try-with-<client>
            latch.await();
        } // try-with-<server>
    }
}
