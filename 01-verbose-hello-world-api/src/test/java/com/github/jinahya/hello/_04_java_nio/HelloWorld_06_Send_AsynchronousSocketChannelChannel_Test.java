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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld#send(AsynchronousSocketChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @deprecated The {@link HelloWorld#send(AsynchronousSocketChannel)} method has been deprecated.
 */
@屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
@Deprecated(forRemoval = true)
@DisplayName("send(AsynchronousServerSocketChannel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_06_Send_AsynchronousSocketChannelChannel_Test extends HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#send(AsynchronousSocketChannel) send(channel)} method throws a
     * {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = (AsynchronousSocketChannel) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(channel)> will throw a NullPointerException
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verfies that {@link HelloWorld#send(AsynchronousSocketChannel) send(channel)} method invokes
     * {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method with the
     * {@code channel} argument, and returns the {@code channel}.
     *
     * @throws Exception if failed.
     */
    @DisplayName("should invoke <write(channel)>")
    @Test
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel)> will return the <channel>
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.<AsynchronousByteChannel>notNull());
        // create, a mock object of <AsynchronousByteChannel>
        final var channel = Mockito.mock(AsynchronousSocketChannel.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.write(channel)> invoked, once
        Mockito.verify(service, Mockito.times(1)).write(channel);
        // assert, <result> is same as <channel>
        Assertions.assertSame(channel, result);
    }

    @畵蛇添足("testing with a real socket doesn't add any value")
    @Test
    void _添足_畵蛇() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel)> will write the <hello, world> bytes
        Mockito.doAnswer(i -> {
                    final var channel = i.getArgument(0, AsynchronousByteChannel.class);
                    for (final var buffer = helloWorldBuffer(); buffer.hasRemaining(); ) {
                        final var w = channel.write(buffer).get();
                        assert w > 0; // why?
                    }
                    return channel;
                })
                .when(service)
                .write(ArgumentMatchers.<AsynchronousByteChannel>notNull());
        // ------------------------------------------------------------------------------------ when
        try (var server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 1);
            log.debug("bound to {}", server.getLocalAddress());
            final var thread = Thread.ofPlatform().start(() -> {
                try (final var client = server.accept().get()) {
                    log.debug("accepted from {}", client.getRemoteAddress());
                    final var b = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (b.hasRemaining()) {
                        try {
                            final var r = client.read(b).get();
                            assert r > 0; // why?
                        } catch (final InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw ie;
                        }
                    }
                    log.debug("decoded: {}", StandardCharsets.US_ASCII.decode(b.flip()));
                } catch (final Exception e) {
                    throw new RuntimeException("failed to accept/read", e);
                }
            });
            try (var client = AsynchronousSocketChannel.open()) {
                final var remote = server.getLocalAddress();
                log.debug("connecting to {}", remote);
                client.connect(remote).get();
                log.debug("connected to {}", client.getRemoteAddress());
                log.debug("sending...");
                final var result = service.send(client);
                log.debug("sent");
                // ---------------------------------------------------------------------------- then
                // verify, <service.write(client)> invoked, once
                Mockito.verify(service, Mockito.times(1)).write(client);
                // assert, <result> is same as <client>
                Assertions.assertSame(client, result);
            } // try-with-<client>
            // join, the <thread> to finish
            thread.join();
        } // try-with-<server>
    }
}