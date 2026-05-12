package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.AsynchronousHelloWorld;
import com.github.jinahya.hello.api.AsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Write_Executor_AsynchronousByteChannel_Attachment__Test
        extends AsynchronousHelloWorldTest {

    @BeforeEach
    void __() { // @formatter:off
        Mockito.doAnswer(i -> {
            var channel = i.getArgument(1, AsynchronousByteChannel.class);
            var attachment = i.getArgument(2);
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
                ArgumentMatchers.<Executor>notNull(),
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
            try (var server = AsynchronousServerSocketChannel.open(group);
                 var executor = Executors.newSingleThreadExecutor(
                         Thread.ofPlatform().name("exec-", 0).factory())) {
                server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                server.accept(null, new CompletionHandler<>() {
                    @Override
                    public void completed(AsynchronousSocketChannel c, Object a) {
                        server.accept(null, this);
                        AsynchronousHelloWorld asynchronousService = asynchronousService();
                        asynchronousService.write(executor, c, c).whenComplete((r, t) -> {
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
            try (var server = AsynchronousServerSocketChannel.open(group);
                 var executor = Executors.newSingleThreadExecutor(
                         Thread.ofPlatform().name("exec-", 0).factory())) {
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
                            asynchronousService().write(executor, client, client)
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
