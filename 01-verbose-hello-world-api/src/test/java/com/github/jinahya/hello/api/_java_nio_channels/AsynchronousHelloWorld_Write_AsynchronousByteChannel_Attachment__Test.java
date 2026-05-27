package com.github.jinahya.hello.api._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.*;

@畵蛇添足
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment__Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment__Test() {
        super(HelloWorld.class);
    }

    @BeforeEach
    void __() { // @formatter:off
        Mockito.doAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousByteChannel.class);
            var attachment = i.getArgument(1);
            var future = new CompletableFuture<>();
            var src = HelloWorldTestUtils.hello_world_byte_buffer();
            channel.write(src, attachment, new CompletionHandler<>() {
                @Override
                public void completed(Integer r, Object a) {
                    if (src.hasRemaining()) {
                        channel.write(src, a, this);
                        return;
                    }
                    future.complete(a);
                }
                @Override
                public void failed(Throwable t, Object a) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        }).when(asynchronousService()).write(
                ArgumentMatchers.<AsynchronousByteChannel>notNull(),
                ArgumentMatchers.any()
        ); // @formatter:on
    }

    @Nested
    class AsynchronousServerSocketChannelTest {

        @Test
        void __() throws Exception { // @formatter:off
            var group = AsynchronousChannelGroup.withCachedThreadPool(
                    Executors.newCachedThreadPool(Thread.ofPlatform().name("ch-", 0).factory()),
                    0);
            try (var server = AsynchronousServerSocketChannel.open(group)) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                server.accept(null, new CompletionHandler<>() {
                    @Override
                    public void completed(AsynchronousSocketChannel c, Object a) {
                        server.accept(null, this);
                        AsynchronousHelloWorld asynchronousService = asynchronousService();
                        asynchronousService.write(c, c).whenComplete((r, t) -> {
                            try { c.close(); } catch (IOException _) { }
                        });
                    }
                    @Override
                    public void failed(Throwable exc, Object a) {
                        // ignored — server close races with pending accept
                    }
                });
                var clients = 4;
                var readFutures = new CompletableFuture<?>[clients];
                for (var i = 0; i < clients; i++) {
                    var future = new CompletableFuture<Void>();
                    readFutures[i] = future;
                    var client = AsynchronousSocketChannel.open(group);
                    client.connect(server.getLocalAddress(), null, new CompletionHandler<>() {
                        @Override
                        public void completed(Void r, Object a) {
                            var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                            client.read(dst, null, new CompletionHandler<>() {
                                @Override
                                public void completed(Integer n, Object a2) {
                                    if (dst.hasRemaining()) {
                                        client.read(dst, null, this);
                                        return;
                                    }
                                    IO.println("[" + Thread.currentThread().getName() + "] "
                                            + StandardCharsets.US_ASCII.decode(dst.flip()));
                                    try { client.close(); } catch (IOException _) { }
                                    future.complete(null);
                                }
                                @Override
                                public void failed(Throwable t, Object a2) {
                                    try { client.close(); } catch (IOException _) { }
                                    future.completeExceptionally(t);
                                }
                            });
                        }
                        @Override
                        public void failed(Throwable exc, Object a) {
                            try { client.close(); } catch (IOException _) { }
                            future.completeExceptionally(exc);
                        }
                    });
                }
                CompletableFuture.allOf(readFutures).get(8L, TimeUnit.SECONDS);
            } finally {
                group.shutdown();
                group.awaitTermination(8L, TimeUnit.SECONDS);
            } // @formatter:on
        }
    }

    @Nested
    class AsynchronousSocketChannelTest {

        @Test
        void __() throws Exception { // @formatter:off
            var group = AsynchronousChannelGroup.withCachedThreadPool(
                    Executors.newCachedThreadPool(Thread.ofPlatform().name("ch-", 0).factory()),
                    0);
            try (var server = AsynchronousServerSocketChannel.open(group)) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                server.accept(null, new CompletionHandler<>() {
                    @Override
                    public void completed(AsynchronousSocketChannel c, Object a) {
                        server.accept(null, this);
                        var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                        c.read(dst, null, new CompletionHandler<>() {
                            @Override
                            public void completed(Integer n, Object a2) {
                                if (dst.hasRemaining()) {
                                    c.read(dst, null, this);
                                    return;
                                }
                                IO.println("[" + Thread.currentThread().getName() + "] "
                                        + StandardCharsets.US_ASCII.decode(dst.flip()));
                                try { c.close(); } catch (IOException _) { }
                            }
                            @Override
                            public void failed(Throwable t, Object a2) {
                                try { c.close(); } catch (IOException _) { }
                            }
                        });
                    }
                    @Override
                    public void failed(Throwable t, Object a) {
                        // ignored — server close races with pending accept
                    }
                });
                var clients = 4;
                var writeFutures = new CompletableFuture<?>[clients];
                for (var i = 0; i < clients; i++) {
                    var future = new CompletableFuture<Void>();
                    writeFutures[i] = future;
                    var client = AsynchronousSocketChannel.open(group);
                    client.connect(server.getLocalAddress(), null, new CompletionHandler<>() {
                        @Override
                        public void completed(Void v, Object a) {
                            asynchronousService().write(client, client)
                                    .whenComplete((r, t) -> {
                                        try { client.close(); } catch (IOException _) { }
                                        if (t != null) {
                                            future.completeExceptionally(t);
                                        } else {
                                            future.complete(null);
                                        }
                                    });
                        }
                        @Override
                        public void failed(Throwable t, Object a) {
                            try { client.close(); } catch (IOException _) { }
                            future.completeExceptionally(t);
                        }
                    });
                }
                CompletableFuture.allOf(writeFutures).get(8L, TimeUnit.SECONDS);
            } finally {
                group.shutdown();
                group.awaitTermination(8L, TimeUnit.SECONDS);
            } // @formatter:on
        }
    }
}
