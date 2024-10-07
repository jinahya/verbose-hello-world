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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A class for testing {@link HelloWorld#send(AsynchronousSocketChannel) send(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @deprecated The {@link HelloWorld#send(AsynchronousSocketChannel) send(channel)} method has been
 * deprecated.
 */
@畵蛇添足
@Deprecated(forRemoval = true)
@DisplayName("send(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_06_Send_AsynchronousSocketChannelChannel_Test extends HelloWorldTest {

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
