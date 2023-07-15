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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, Executor) writeAsync(channel,
 * position, executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_21_WriteAsync_AsynchronousByteChannelWithExecutor_Arguments_Test
 */
@DisplayName("write(channel, position, executor)")
@Slf4j
class HelloWorld_31_WriteAsync_AsynchronousFileChannelWithExecutor_Test
        extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, Executor) write(channel,
     * position, executor)} method returns a future whose result is same as given {@code channel},
     * and asserts {@value HelloWorld#BYTES} bytes has been written to the {@code channel} starting
     * at {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("(channel, position, executor) -> channel.write(buffer, >= position)+")
    @Test
    void __() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        doAnswer(w -> {
            var future = mock(Future.class);
            when(future.get()).thenAnswer(g -> {
                var buffer = w.getArgument(0, ByteBuffer.class);
                var position = w.getArgument(1, Long.class);
                var written = current().nextInt(1, buffer.remaining() + 1);
                buffer.position(buffer.position() + written);
                writtenSoFar.add(written);
                return written;
            });
            return future;
        }).when(channel).write(argThat(b -> b != null && b.hasRemaining()), longThat(p -> p >= 0L));
        var position = current().nextLong(0, Long.MAX_VALUE - BYTES);
        var executor = newSingleThreadExecutor();
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.writeAsync(channel, position, executor);
        var result = future.get();
        // ------------------------------------------------------------------------------------ THEN
        verify(service, times(1)).write(channel, position);
        assertSame(channel, result);
    }
}