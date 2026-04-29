package com.github.jinahya.hello.api._java_nio_channels;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_AsynchronousByteChannel_畵蛇添足_Test
        extends HelloWorldTest {

    @BeforeEach
    void __() throws ExecutionException, InterruptedException {
        HelloWorldTestUtils.write_asynchornousbytechannel_writes_hello_world(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class SendHelloWorldTest {

        @Test
        void __() throws IOException, ExecutionException, InterruptedException {
            try (var server = AsynchronousServerSocketChannel.open()) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                final var thread = Thread.ofPlatform().start(() -> {
                    try (var client = server.accept().get()) {
                        final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                        while (dst.hasRemaining()) {
                            client.read(dst).get();
                        }
                        log.debug("received: {}", StandardCharsets.US_ASCII.decode(dst.flip()));
                    } catch (final Exception e) {
                        if (e instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        }
                        throw new RuntimeException(e);
                    }
                });
                try (var client = AsynchronousSocketChannel.open()) {
                    client.connect(server.getLocalAddress()).get();
                    service().write(client);
                }
                thread.join();
            }
        }
    }
}
