package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.ValidationProxy.newValidationProxy;
import static java.net.InetAddress.getLocalHost;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
class AsynchronousHelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------
    private static AsynchronousServerSocketChannel SERVER_SOCKET_CHANNEL;

    private static SocketAddress SERVER_SOCKET_ADDRESS;

    private static CountDownLatch LATCH = new CountDownLatch(2);

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeAll
    private static void openServerSocketChannel() throws IOException {
        SERVER_SOCKET_CHANNEL = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(0));
        SERVER_SOCKET_ADDRESS = SERVER_SOCKET_CHANNEL.getLocalAddress();
        SERVER_SOCKET_CHANNEL.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(final AsynchronousSocketChannel socketChannel, final Void attachment) {
                final ByteBuffer buffer = allocate(BYTES);
                socketChannel.read(buffer, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(final Integer result, final Void attachment) {
                        if (!buffer.hasRemaining()) {
                            buffer.flip();
                            log.debug("buffer: {}", US_ASCII.decode(buffer).toString());
                            try {
                                socketChannel.close();
                                LATCH.countDown();
                            } catch (final IOException ioe) {
                                log.debug("failed to close socket channel", ioe);
                            }
                        } else {
                            socketChannel.read(buffer, null, this);
                        }
                    }

                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.debug("failed to read", exc);
                    }
                });
                LATCH.countDown();
                SERVER_SOCKET_CHANNEL.accept(null, this);
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                if (!(exc instanceof AsynchronousCloseException)) {
                    log.debug("failed to accept", exc);
                }
                LATCH.countDown();
            }
        });
    }

    @AfterAll
    private static void closeServerSocketChannel() throws InterruptedException, IOException {
//        final boolean broken = LATCH.await(20L, TimeUnit.SECONDS);
//        if (!broken) {
//            log.error("times up for awaiting the latch");
//        }
        SERVER_SOCKET_CHANNEL.close();
        log.debug("server socket channel closed");
    }

    // ------------------------------------------------------------------------------------------------ AsyncFileChannel
    @Test
    void testAppend(@TempDir final Path tempDir) throws Exception {
        final Path path = createTempFile(tempDir, null, null);
        try (FileChannel channel = FileChannel.open(path, APPEND)) {
            channel.write(allocate(current().nextInt(1, 8)));
            channel.force(false);
        }
        final long size = size(path);
        try (AsynchronousFileChannel channel = open(path, WRITE)) {
            assertSame(channel, helloWorld.append(channel));
        }
        assertEquals(size + BYTES, size(path));
    }

    @Test
    void testAppendAsync(@TempDir final Path tempDir) throws Exception {
        final Path path = createTempFile(tempDir, null, null);
        try (FileChannel channel = FileChannel.open(path, APPEND)) {
            channel.write(allocate(current().nextInt(1, 8)));
            channel.force(false);
        }
        final long size = size(path);
        try (AsynchronousFileChannel channel = open(path, WRITE)) {
            CompletableFuture<AsynchronousFileChannel> future = helloWorld.appendAsync(channel);
            final AsynchronousFileChannel actual = future.get();
            assertEquals(channel, actual);
        }
        assertEquals(size + BYTES, size(path));
    }

    // --------------------------------------------------------------------------------------- AsynchronousSocketChannel
    @Test
    void testSend() throws Exception {
        final AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        final SocketAddress remote = new InetSocketAddress(
                getLocalHost(), ((InetSocketAddress) SERVER_SOCKET_ADDRESS).getPort());
        socketChannel.connect(remote, null, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(final Void result, final Void attachment) {
                try {
                    log.debug("connected to {}", socketChannel.getRemoteAddress());
                } catch (final IOException ioe) {
                    log.debug("failed to get remote address", ioe);
                }
                try {
                    assertSame(socketChannel, helloWorld.send(socketChannel));
                    log.debug("sent");
                    socketChannel.close();
                    log.debug("closed");
                } catch (final Exception e) {
                    log.debug("failed to send", e);
                }
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                log.debug("failed to connect", exc);
            }
        });
    }

    @Test
    void testSendAsync() throws Exception {
        final AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        final SocketAddress remote = new InetSocketAddress(
                getLocalHost(), ((InetSocketAddress) SERVER_SOCKET_ADDRESS).getPort());
        socketChannel.connect(remote, null, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(final Void result, final Void attachment) {
                try {
                    log.debug("connected to {}", socketChannel.getRemoteAddress());
                } catch (final IOException ioe) {
                    log.debug("failed to get remote address", ioe);
                }
                try {
                    assertSame(socketChannel, helloWorld.sendAsync(socketChannel).get());
                    log.debug("sent");
                    socketChannel.close();
                    log.debug("closed");
                } catch (final Exception e) {
                    log.debug("failed to send", e);
                }
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                log.debug("failed to connect", exc);
            }
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a validation proxy of {@link #helloWorld} whose method arguments and result are validated.
     *
     * @return a proxy of {@link #helloWorld}.
     */
    AsynchronousHelloWorld helloWorld() {
        return newValidationProxy(AsynchronousHelloWorld.class, helloWorld);
    }

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer)}} method of {@link Spy spied} {@code helloWorld} instance to return given
     * buffer.
     */
    @BeforeEach
    private void stubPutBufferReturnsTheBuffer() throws IOException {
        doAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            buffer.position(buffer.position() + BYTES);
            return buffer;
        }).when(helloWorld).put(any(ByteBuffer.class));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    AsynchronousHelloWorld helloWorld;
}
