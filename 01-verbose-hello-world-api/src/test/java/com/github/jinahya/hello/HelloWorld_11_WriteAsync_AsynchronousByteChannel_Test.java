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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_11_WriteAsync_AsynchronousByteChannel_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService)
     * writeAsync(channel, service)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)}
     * method and writes the buffer to the {@code channel}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeAsync(channel, service) invokes put(buffer)"
                 + " and writes the buffer to channel")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_(
            @TempDir final Path tempDir)
            throws InterruptedException, ExecutionException {
        final AsynchronousByteChannel channel = Mockito.mock(
                AsynchronousByteChannel.class);
        Mockito.lenient().when(channel.write(ArgumentMatchers.notNull()))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final int written
                            = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    @SuppressWarnings({"unchecked"})
                    final Future<Integer> future = Mockito.mock(Future.class);
                    Mockito.doReturn(written).when(future).get();
                    return future;
                });
        final ExecutorService service = Executors.newSingleThreadExecutor();
        final Future<AsynchronousByteChannel> future
                = helloWorld().writeAsync(channel, service);
        final AsynchronousByteChannel actual = future.get();
        Assertions.assertSame(channel, actual);
        // TODO: Implement!
    }
}
