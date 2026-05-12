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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Write_AsynchronousByteChannel__Test extends HelloWorldTest {

    @BeforeEach
    void __() throws ExecutionException, InterruptedException {
        HelloWorldTestUtils.write_asynchornousbytechannel_writes_hello_world(service());
    }

    @Nested
    class AsynchronousServerSocketChannelTest {

        @Test
        void __() throws Exception { // @formatter:off
            var group = AsynchronousChannelGroup.withThreadPool(
                    Executors.newCachedThreadPool(Thread.ofPlatform().name("ch-", 0).factory())
            );
            try (var server = AsynchronousServerSocketChannel.open(group)) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                Thread.ofPlatform().start(() -> {
                    while (server.isOpen()) {
                        try {
                            var c = server.accept().get();
                            Thread.ofPlatform().start(() -> {
                                try (c) { service().write(c); } catch (Exception _) { }
                            });
                        } catch (Exception _) {
                            return;
                        }
                    }
                });
                var clients = 4;
                var threads = new Thread[clients];
                for (var i = 0; i < clients; i++) {
                    threads[i] = Thread.ofPlatform().start(() -> {
                        try (var client = AsynchronousSocketChannel.open(group)) {
                            client.connect(server.getLocalAddress()).get();
                            var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                            while (dst.hasRemaining()) {
                                client.read(dst).get();
                            }
                            IO.println("[" + Thread.currentThread().getName() + "] "
                                    + StandardCharsets.US_ASCII.decode(dst.flip()));
                        } catch (Exception t) {
                            throw new RuntimeException(t);
                        }
                    });
                }
                for (var t : threads) {
                    t.join();
                }
            } finally {
                group.shutdown();
                if (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                    log.warn("channel group did not terminate within 8s");
                }
            } // @formatter:on
        }
    }

    @Nested
    class AsynchronousSocketChannelTest {

        @Test
        void __() throws Exception { // @formatter:off
            var group = AsynchronousChannelGroup.withThreadPool(
                    Executors.newCachedThreadPool(Thread.ofPlatform().name("ch-", 0).factory())
            );
            try (var server = AsynchronousServerSocketChannel.open(group)) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                Thread.ofPlatform().start(() -> {
                    while (server.isOpen()) {
                        try {
                            var c = server.accept().get();
                            Thread.ofPlatform().start(() -> {
                                try (c) {
                                    var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                                    while (dst.hasRemaining()) {
                                        c.read(dst).get();
                                    }
                                    IO.println("[" + Thread.currentThread().getName() + "] "
                                            + StandardCharsets.US_ASCII.decode(dst.flip()));
                                } catch (Exception _) { }
                            });
                        } catch (Exception _) {
                            return;
                        }
                    }
                });
                var clients = 4;
                var threads = new Thread[clients];
                for (var i = 0; i < clients; i++) {
                    threads[i] = Thread.ofPlatform().start(() -> {
                        try (var client = AsynchronousSocketChannel.open(group)) {
                            client.connect(server.getLocalAddress()).get();
                            service().write(client); // blocks on Future.get() internally
                        } catch (Exception t) {
                            throw new RuntimeException(t);
                        }
                    });
                }
                for (var t : threads) {
                    t.join();
                }
            } finally {
                group.shutdown();
                if (!group.awaitTermination(8L, TimeUnit.SECONDS)) {
                    log.warn("channel group did not terminate within 8s");
                }
            } // @formatter:on
        }
    }
}
