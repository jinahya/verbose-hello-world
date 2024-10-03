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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#write(WritableByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_02_Write_WritableByteChannel_Test extends HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (WritableByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(channel)> throws a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies {@link HelloWorld#write(WritableByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the {@code buffer} to specified {@code channel}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <put(buffer[12])>
            and writes the <buffer> to the <channel> while the <buffer> has <remaining>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase the <buffer>'s <position> by <12>
        stub_put_buffer_will_increase_buffer_position_by_12();
        final var channel = Mockito.mock(WritableByteChannel.class);
        // stub, <channel.write(buffer)> will increase the <buffer>'s <position> by a random value
        Mockito.doAnswer(i -> {
                    final var src = i.getArgument(0, ByteBuffer.class);
                    final var written = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                    src.position(src.position() + written);
                    return written;
                })
                .when(channel)
                .write(ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        // verify, <put(buffer[12])> invoked, once
        final var buffer = verify_put_buffer12_invoked_once();
        JavaNioByteBufferUtils.print(buffer);
        // verify, <channel.write(buffer)> invoked, at least once

        // assert, <buffer> has no <remaining>

        // assert, <result> is same as <channel>
        Assertions.assertSame(channel, result);
    }

    @畵蛇添足("SocketChannel implements WritableByteChannel")
    @DisplayName("send(SocketChannel)")
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel)> will write the 'hello, world' bytes
        Mockito.doAnswer(i -> {
                    final var channel = i.getArgument(0, WritableByteChannel.class);
                    final var array = "hello, world".getBytes(StandardCharsets.US_ASCII);
                    for (final var b = ByteBuffer.wrap(array); b.hasRemaining(); ) {
                        channel.write(b);
                    }
                    return channel;
                })
                .when(service)
                .write(ArgumentMatchers.<WritableByteChannel>notNull());
        // start a new thread which
        //           binds to a random port
        //           accepts a client,
        //           and reads 12 bytes
        final var addr = InetAddress.getLoopbackAddress();
        final var port = new ArrayBlockingQueue<Integer>(1);
        Thread.ofPlatform().name("server").start(() -> {
            try (var server = ServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(addr, 0), 1);
                log.debug("bound to {}", server.getLocalAddress());
                port.offer(((InetSocketAddress) server.getLocalAddress()).getPort());
                try (var client = server.accept()) {
                    log.debug("accepted; remote: {}, local: {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    log.debug("reading {} bytes...", buffer.remaining());
                    while (buffer.hasRemaining()) {
                        client.read(buffer);
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
        try (var client = SocketChannel.open()) {
            final var remote = new InetSocketAddress(addr, port.take());
            log.debug("connecting to {}", remote);
            client.connect(remote);
            log.debug("connected: remote: {}, local: {}", client.getRemoteAddress(),
                      client.getLocalAddress());
            log.debug("writing...");
            service.write(client);
            log.debug("written.");
        }
        // ------------------------------------------------------------------------------------ then
        // empty
    }
}
