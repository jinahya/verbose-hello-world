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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_12_WriteAsync_AsynchronousFileChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_12_WriteAsync_AsynchronousFileChannel_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService) writeAsync(channel,
     * posotion, service)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method and writes the buffer to
     * {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeAsync(channel, position, service) invokes put(buffer) and writes the buffer to channel")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        final AsynchronousFileChannel channel = Mockito.mock(AsynchronousFileChannel.class);
        final LongAdder writtenSoFar = new LongAdder();
        Mockito.lenient()
                .when(channel.write(ArgumentMatchers.any(ByteBuffer.class), ArgumentMatchers.anyLong()))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final long position = i.getArgument(1);
                    final int written = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    writtenSoFar.add(written);
                    return CompletableFuture.completedFuture(written);
                });
        final ExecutorService service = Executors.newSingleThreadExecutor();
        final Future<AsynchronousFileChannel> future = helloWorld().writeAsync(channel, 0L, service);
        final AsynchronousFileChannel actual = future.get();
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService) writeAsync(channel,
     * posotion, service)} method writes {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes to {@code channel}
     * from specified position.
     *
     * @param tempDir a temporary directory to test with.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeAsync(channel, position, service) invokes put(buffer) and writes the buffer to channel")
    @Test
    void writeAsync_Writes12BytesFromPosition_(@TempDir final Path tempDir)
            throws IOException, InterruptedException, ExecutionException {
        final Path file = Files.createTempFile(tempDir, null, null);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            final long position = new Random().nextInt(1024);
            final ExecutorService service = Executors.newSingleThreadExecutor();
            final Future<AsynchronousFileChannel> future = helloWorld().writeAsync(channel, position, service);
            final AsynchronousFileChannel actual = future.get();
            // TODO: Implement!
        }
    }
}
