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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Test extends HelloWorldTest {

    // TODO: Remove following stubbing when you implemented the put(ByteBuffer) method!
    @BeforeEach
    void beforeEach() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        Mockito.doAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0);
            buffer.position(buffer.position() + HelloWorld.BYTES);
            return buffer;
        }).when(helloWorld()).put(ArgumentMatchers.any(ByteBuffer.class));
    }

    /**
     * Asserts {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel, position)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method and writes the buffer to {@code channel}
     * starting at specified {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel, position) invokes put(buffer) and writes the buffer to channel")
    @Test
    void writeCompletable_InvokePutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        final LongAdder writtenSoFar = new LongAdder();
        final AsynchronousFileChannel channel = Mockito.mock(AsynchronousFileChannel.class);
        Mockito.doAnswer(i -> {
            final ByteBuffer src = i.getArgument(0);
            final long position = i.getArgument(1);
            final Long attachment = i.getArgument(2);
            final CompletionHandler<Integer, Long> handler = i.getArgument(3);
            final int written = new Random().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            handler.completed(written, attachment);
            return null;
        }).when(channel).write(ArgumentMatchers.any(ByteBuffer.class), ArgumentMatchers.longThat(a -> a >= 0L),
                               ArgumentMatchers.anyLong(), ArgumentMatchers.<CompletionHandler<Integer, Long>>any());
        final long position = 0L;
        final Future<AsynchronousFileChannel> future = helloWorld().writeCompletable(channel, position);
        final AsynchronousFileChannel actual = future.get();
        Mockito.verify(helloWorld(), Mockito.times(1)).put(bufferCaptor().capture());
        final ByteBuffer buffer = bufferCaptor().getValue();
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        Assertions.assertFalse(buffer.hasRemaining());
        Mockito.verify(channel, Mockito.atLeast(1))
               .write(ArgumentMatchers.same(buffer), ArgumentMatchers.longThat(a -> a >= 0L),
                      ArgumentMatchers.anyLong(), ArgumentMatchers.<CompletionHandler<Integer, Long>>any());
        Assertions.assertSame(channel, actual);
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue());
    }

    /**
     * Asserts {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel, posotion)}
     * method writes {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to {@code channel} starting at specified
     * position.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel, position, service) writes 12 bytes starting at position")
    @Test
    void writeCompletable_Writes12BytesFromPosition_(@TempDir final Path tempDir)
            throws IOException, InterruptedException, ExecutionException {
        final Path file = Files.createTempFile(tempDir, null, null);
        final long position = new Random().nextInt(1024);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            helloWorld().writeCompletable(channel, position).get().force(false);
        }
        try (RandomAccessFile f = new RandomAccessFile(file.toFile(), "r")) {
            f.seek(position);
            for (int i = 0; i < HelloWorld.BYTES; i++) {
                final int r = f.read();
                Assertions.assertNotEquals(-1, r);
            }
        }
    }
}
