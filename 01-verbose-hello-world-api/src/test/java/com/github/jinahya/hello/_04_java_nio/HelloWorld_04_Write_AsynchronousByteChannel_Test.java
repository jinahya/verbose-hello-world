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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_04_Write_AsynchronousByteChannel_Test extends HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = (AsynchronousByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(channel)> will throw a NullPointerException
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("""
            should invoke put(buffer[12])
            and write the <buffer> to the <channel> while the the <buffer> has remaining"""
    )
    @Test
    void __() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase <buffer>'s <position> by <HelloWorld.BYTES>
        BDDMockito.willAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var writtenSoFar = new LongAdder();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        // stub, <channel.write(buffer)> will return a <future> drains the <buffer>
        final var futureReference = new AtomicReference<Future<Integer>>();
        BDDMockito.willAnswer(w -> {
            // preceding future's get() should be invoked
            final var previousFuture = futureReference.get();
            if (previousFuture != null) {
                Mockito.verify(previousFuture, Mockito.times(1)).get();
            }
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            // stub, <future.get()> will increase <buffer>'s <position> by a random value
            BDDMockito.willAnswer(g -> {
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                writtenSoFar.add(result);
                return result;
            }).given(future).get();
            futureReference.set(future);
            return future;
        }).given(channel).write(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.put(buffer[12])> invoked, once
        final var bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor.capture());
        final var buffer = bufferCaptor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // verify, <channel.write(buffer)> invoked, at least once

        // assert, <buffer> has no <remaining>

        // assert, total number of bytes written is equal to <HelloWorld.BYTES>

        // assert, <result> is same as <channel>
        Assertions.assertSame(channel, result);
    }

    @DisplayName("send(AsynchronousSocketChannel)")
    @畵蛇添足
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var channel = i.getArgument(0, AsynchronousByteChannel.class);
                    final var array = "hello, world".getBytes(StandardCharsets.US_ASCII);
                    for (final var b = ByteBuffer.wrap(array); b.hasRemaining(); ) {
                        channel.write(b).get();
                    }
                    return channel;
                })
                .given(service)
                .write(ArgumentMatchers.<AsynchronousByteChannel>notNull());
        // start a new thread which
        //           binds to a random port
        //           accepts a client,
        //           and reads 12 bytes
        final var addr = InetAddress.getLoopbackAddress();
        final var port = new ArrayBlockingQueue<Integer>(1);
        Thread.ofPlatform().name("server").start(() -> {
            try (var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(addr, 0), 1);
                log.debug("bound to {}", server.getLocalAddress());
                port.offer(((InetSocketAddress) server.getLocalAddress()).getPort());
                try (var client = server.accept().get()) {
                    log.debug("accepted from {} through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    log.debug("reading {} bytes...", buffer.remaining());
                    while (buffer.hasRemaining()) {
                        client.read(buffer).get();
                    }
                    buffer.flip();
                    log.debug("decoded: {}", StandardCharsets.US_ASCII.decode(buffer));
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
        // ------------------------------------------------------------------------------------ when
        // connect to the <server>,
        //       and send <12> bytes to the <server>
        Thread.currentThread().setName("client");
        try (var client = AsynchronousSocketChannel.open()) {
            final var remote = new InetSocketAddress(addr, port.take());
            log.debug("connecting to {}", remote);
            client.connect(remote).get();
            log.debug("connected to {} through {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            log.debug("writing...");
            service.write(client); // AsynchronousSocketChannel implements AsynchronousByteChannel
            log.debug("written");
        }
        // ------------------------------------------------------------------------------------ then
        // empty
    }
}
