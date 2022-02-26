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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Test
        extends HelloWorldTest {

    // TODO: Remove this stubbing method when you implemented the put(buffer) method!
    @BeforeEach
    void stub_PutBuffer_FillBuffer() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        doAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0);
            buffer.position(buffer.position() + BYTES);
            return buffer;
        }).when(helloWorld()).put(any());
    }

    /**
     * Asserts {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long)
     * writeCompletable(channel, position)} method invokes {@link HelloWorld#put(ByteBuffer)
     * put(buffer)} method and writes the buffer to {@code channel} starting at specified {@code
     * position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel, position)"
                 + " invokes put(buffer)"
                 + " and writes to channel")
    @Test
    void writeCompletable_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        final LongAdder writtenSoFar = new LongAdder();
        final AsynchronousFileChannel channel
                = mock(AsynchronousFileChannel.class);
        doAnswer(i -> {
            final ByteBuffer src = i.getArgument(0);
            final long position = i.getArgument(1);
            final Long attachment = i.getArgument(2);
            final CompletionHandler<Integer, Long> handler = i.getArgument(3);
            final int written = new Random().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            handler.completed(written, attachment);
            return null;
        }).when(channel).write(notNull(),
                               longThat(a -> a >= 0L),
                               notNull(),
                               notNull());
        final long position = 0L;
        final Future<AsynchronousFileChannel> future
                = helloWorld().writeCompletable(channel, position);
        final AsynchronousFileChannel actual = future.get();
        verify(helloWorld(), times(1))
                .put(bufferCaptor().capture());
        final ByteBuffer buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        assertFalse(buffer.hasRemaining());
        verify(channel, atLeast(1))
                .write(same(buffer),
                       longThat(a -> a >= position),
                       notNull(),
                       notNull());
        assertSame(channel, actual);
        assertEquals(BYTES, writtenSoFar.intValue());
    }

    /**
     * Asserts {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long)
     * writeCompletable(channel, posotion)} method writes {@link HelloWorld#BYTES} bytes to {@code
     * channel} starting at specified position.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel, position)" +
                 " writes 12 bytes starting at position")
    @Test
    void writeCompletable_Write12BytesFromPosition_(
            @TempDir final Path tempDir)
            throws IOException, InterruptedException, ExecutionException {
        final Path path = createTempFile(tempDir, null, null);
        final long position = new Random().nextInt(1024);
        try (AsynchronousFileChannel channel = open(path, WRITE)) {
            helloWorld().writeCompletable(channel, position)
                    .get()
                    .force(false);
        }
        assertEquals(position + BYTES, size(path));
        final AsynchronousFileChannel channel = open(path, READ);
        final ByteBuffer buffer = allocate(BYTES);
        final CompletableFuture<Void> future = new CompletableFuture<>();
        channel.read(buffer,                                  // buffer
                     position,                                // position
                     position,                                // attachment
                     new CompletionHandler<Integer, Long>() { // handler
                         @Override
                         public void completed(final Integer result,
                                               Long attachment) {
                             if (!buffer.hasRemaining()) {
                                 future.complete(null);
                                 return;
                             }
                             attachment += result;
                             channel.read(buffer,     // buffer
                                          attachment, // position
                                          attachment, // attachment
                                          this);      // handler
                         }

                         @Override
                         public void failed(final Throwable exc,
                                            final Long attachment) {
                             log.error("failed to read from channel" +
                                       "; attachment: {}", attachment, exc);
                             future.completeExceptionally(exc);
                         }
                     });
        future.get();
        assertFalse(buffer.hasRemaining());
        channel.close();
    }
}
