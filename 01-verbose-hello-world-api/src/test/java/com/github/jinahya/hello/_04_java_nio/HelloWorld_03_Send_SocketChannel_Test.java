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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#send(SocketChannel) send(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @deprecated The {@link HelloWorld#send(SocketChannel) send(channel)} method has been deprecated.
 */
@屋上架屋("SocketChannel implements WritableByteChannel")
@Deprecated(forRemoval = true)
@DisplayName("send(SocketChannel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_03_Send_SocketChannel_Test extends HelloWorldTest {

    @DisplayName("send(SocketChannel)")
    @Test
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel)> will write the 'hello, world' bytes
        Mockito.doAnswer(i -> {
                    final var channel = i.getArgument(0, WritableByteChannel.class);
                    final var array = "hello, world".getBytes(StandardCharsets.US_ASCII);
                    for (final var b = ByteBuffer.wrap(array); b.hasRemaining(); ) {
                        final var written = channel.write(b);
                        assert written >= 0; // why?
                    }
                    return channel;
                })
                .when(service)
                .write(ArgumentMatchers.<WritableByteChannel>notNull());
        // -----------------------------------------------------------------------------------------
        // start a new thread which
        //           binds to a random port
        //           accepts a client,
        //           and reads 12 bytes
        Thread.currentThread().setName("server");
        try (var server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 1);
            log.debug("bound to {}", server.getLocalAddress());
            // -------------------------------------------------------------------------------------
            // accept a <client>
            //           read 12 bytes
            Thread.ofPlatform().name("server").start(() -> {
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
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
            // -------------------------------------------------------------------------------------
            // connect to the <server>,
            //       and send <hello, world> to the <server>
            Thread.currentThread().setName("client");
            try (var client = SocketChannel.open()) {
                final var remote = server.getLocalAddress();
                log.debug("connecting to {}", remote);
                client.connect(remote);
                log.debug("connected: remote: {}, local: {}", client.getRemoteAddress(),
                          client.getLocalAddress());
                log.debug("writing...");
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client);
                log.debug("written.");
                // ---------------------------------------------------------------------------- then
                Mockito.verify(service, Mockito.times(1)).write(client);
                Assertions.assertSame(client, result);
            }
        }
    }
}
