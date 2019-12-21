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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.ValidationProxy.newValidationProxy;
import static java.net.InetAddress.getLocalHost;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.US_ASCII;
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
    private static AsynchronousServerSocketChannel CHANNEL;

    private static SocketAddress ADDRESS;

    private static CountDownLatch LATCH = new CountDownLatch(2);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Opens the {@link #CHANNEL} and starts accepting.
     *
     * @throws IOException if an I/O error occurs.
     */
    @BeforeAll
    private static void openServerSocketChannel() throws IOException {
        CHANNEL = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(0));
        log.debug("server socket channel is open: {}", CHANNEL);
        ADDRESS = CHANNEL.getLocalAddress();
        CHANNEL.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(final AsynchronousSocketChannel channel, final Void attachment) {
                log.debug("accepted: {}", channel);
                final ByteBuffer buffer = allocate(BYTES);
                channel.read(buffer, null, new CompletionHandler<Integer, Void>() {

                    @Override
                    public void completed(final Integer read, final Void attachment) {
                        assert read != null;
                        if (read == -1) {
                            log.debug("channel is closed");
                            return;
                        }
                        if (!buffer.hasRemaining()) { // all 12 bytes are read
                            buffer.flip();
                            log.debug("buffer: {}", US_ASCII.decode(buffer).toString());
                            try {
                                channel.close();
                            } catch (final IOException ioe) {
                                log.debug("failed to close socket channel", ioe);
                            }
                            LATCH.countDown();
                            return;
                        }
                        channel.read(buffer, null, this); // keep reading
                    }

                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.debug("failed to read", exc);
                        LATCH.countDown();
                    }
                });
                CHANNEL.accept(attachment, this); // keep accepting
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                if (!(exc instanceof AsynchronousCloseException)) {
                    log.debug("failed to accept", exc);
                }
                if (CHANNEL.isOpen()) {
                    CHANNEL.accept(attachment, this); // keep accepting
                }
                LATCH.countDown();
            }
        });
    }

    /**
     * Closes the {@link #CHANNEL} after awaiting the {@link #LATCH} for some time.
     *
     * @throws InterruptedException if interrupted while awaiting the {@link #LATCH}.
     * @throws IOException          if an I/O error occurs.
     */
    @AfterAll
    private static void closeServerSocketChannel() throws InterruptedException, IOException {
        final boolean broken = LATCH.await(10L, TimeUnit.SECONDS);
        if (!broken) {
            log.error("times up while awaiting the latch to be broken");
        }
        CHANNEL.close();
        log.debug("server socket channel closed");
    }

    // ------------------------------------------------------------------------------------------------ AsyncFileChannel

    /**
     * Tests {@link AsynchronousHelloWorld#append(AsynchronousFileChannel)} method.
     *
     * @param tempDir a temporary directory to test with.
     * @throws Exception if an I/O error occurs.
     */
    @Test
    void testAppend(@TempDir final Path tempDir) throws Exception {
        final Path path = Files.createTempFile(tempDir, null, null);
        try (FileChannel channel = FileChannel.open(path, APPEND)) {
            channel.write(allocate(current().nextInt(1, 8)));
            channel.force(false);
        }
        final long size = Files.size(path);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, WRITE)) {
            assertSame(channel, helloWorld.append(channel));
        }
        assertEquals(size + BYTES, Files.size(path));
    }

    /**
     * Tests {@link AsynchronousHelloWorld#appendAsync(AsynchronousFileChannel)} #append(Path)} method.
     *
     * @param tempDir a temporary directory to test with.
     * @throws Exception if an error occurs.
     */
    @Test
    void testAppendAsync(@TempDir final Path tempDir) throws Exception {
        final Path path = Files.createTempFile(tempDir, null, null);
        try (FileChannel channel = FileChannel.open(path, APPEND)) {
            channel.write(allocate(current().nextInt(1, 8)));
            channel.force(false);
        }
        final long size = Files.size(path);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, WRITE)) {
            assertEquals(channel, helloWorld.appendAsync(channel).get());
        }
        assertEquals(size + BYTES, Files.size(path));
    }

    // --------------------------------------------------------------------------------------- AsynchronousSocketChannel

    /**
     * Tests {@link AsynchronousHelloWorld#send(AsynchronousSocketChannel)} method.
     *
     * @throws Exception if an I/O error occurs.
     */
    @Test
    void testSend() throws Exception {
        final AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        final SocketAddress remote = new InetSocketAddress(
                getLocalHost(), ((InetSocketAddress) ADDRESS).getPort());
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

    /**
     * Tests {@link AsynchronousHelloWorld#sendAsync(AsynchronousSocketChannel)} method.
     *
     * @throws Exception if an I/O error occcurs.
     */
    @Test
    void testSendAsync() throws Exception {
        final AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        final SocketAddress remote = new InetSocketAddress(
                getLocalHost(), ((InetSocketAddress) ADDRESS).getPort());
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
    private void stubPutBufferReturnsTheBuffer() {
        doAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            buffer.position(buffer.position() + BYTES);
            return buffer;
        })
                .when(helloWorld).put(any(ByteBuffer.class));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    AsynchronousHelloWorld helloWorld;
}
