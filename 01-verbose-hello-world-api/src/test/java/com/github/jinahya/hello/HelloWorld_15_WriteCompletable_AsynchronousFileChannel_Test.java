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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Test extends HelloWorldTest {

    // TODO: Remove this stubbing method when you implemented the put(buffer) method!
    @BeforeEach
    void stub_PutBuffer_FillBuffer() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        Mockito.doAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            buffer.position(buffer.position() + HelloWorld.BYTES);
            return buffer;
        }).when(helloWorld()).put(any());
    }

    /**
     * Asserts
     * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel,
     * position)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte
     * buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes, and writes the buffer to
     * {@code channel} starting at specified {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel, position)"
                 + " invokes put(buffer(12))"
                 + ", and writes the buffer to channel")
    @Test
    void writeCompletable_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        var writtenSoFar = new LongAdder();
        var channel = mock(AsynchronousFileChannel.class);
        Mockito.doAnswer(i -> {
                    ByteBuffer src = i.getArgument(0);
                    var position = i.getArgument(1);
                    Long attachment = i.getArgument(2);
                    CompletionHandler<Integer, Long> handler = i.getArgument(3);
                    var written = ThreadLocalRandom.current().nextInt(src.remaining() + 1);
                    src.position(src.position() + written);
                    writtenSoFar.add(written);
                    handler.completed(written, attachment);
                    return null;
                })
                .when(channel)
                .write(ArgumentMatchers.notNull(), ArgumentMatchers.longThat(a -> a >= 0L),
                       ArgumentMatchers.notNull(), ArgumentMatchers.notNull());
        var position = 0L;
        var future = helloWorld().writeCompletable(channel, position);
        var actual = future.get();
        Mockito.verify(helloWorld(), Mockito.times(1))
                .put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        Assertions.assertFalse(buffer.hasRemaining());
        Mockito.verify(channel, Mockito.atLeast(1))
                .write(same(buffer),
                       ArgumentMatchers.longThat(a -> a >= position),
                       ArgumentMatchers.notNull(),
                       ArgumentMatchers.notNull());
        Assertions.assertSame(channel, actual);
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue());
    }

    /**
     * Asserts
     * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel,
     * posotion)} method writes {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to
     * {@code channel} starting at specified position.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel, position)" +
                 " writes 12 bytes starting at position")
    @Test
    void writeCompletable_Write12BytesFromPosition_(@TempDir Path tempDir)
            throws IOException, InterruptedException, ExecutionException {
        var path = Files.createTempFile(tempDir, null, null);
        var position = ThreadLocalRandom.current().nextLong(1024L);
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            helloWorld().writeCompletable(channel, position)
                    .get()
                    .force(false);
        }
        Assertions.assertEquals(position + HelloWorld.BYTES, Files.size(path));
        var channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
        var future = new CompletableFuture<Void>();
        channel.read(buffer,                     // dst
                     position,                   // position
                     position,                   // attachment
                     new CompletionHandler<>() { // handler
                         @Override
                         public void completed(Integer result, Long attachment) {
                             if (!buffer.hasRemaining()) {
                                 future.complete(null);
                                 return;
                             }
                             attachment += result;
                             channel.read(buffer,     // dst
                                          attachment, // position
                                          attachment, // attachment
                                          this);      // handler
                         }

                         @Override
                         public void failed(Throwable exc, Long attachment) {
                             log.error("failed to read from channel" +
                                       "; attachment: {}", attachment, exc);
                             future.completeExceptionally(exc);
                         }
                     });
        future.get();
        Assertions.assertFalse(buffer.hasRemaining());
        channel.close();
    }
}
