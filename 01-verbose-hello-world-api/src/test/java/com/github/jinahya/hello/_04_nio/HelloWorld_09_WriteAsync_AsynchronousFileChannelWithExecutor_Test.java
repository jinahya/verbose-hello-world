package com.github.jinahya.hello._04_nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, Executor) writeAsync(channel,
 * position, executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_WriteAsync_AsynchronousFileChannelWithExecutor_Arguments_Test
 */
@DisplayName("write(channel, position, executor)")
@Slf4j
class HelloWorld_09_WriteAsync_AsynchronousFileChannelWithExecutor_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach()
            throws InterruptedException, ExecutionException {
        willAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            for (var src = allocate(BYTES); src.hasRemaining(); ) {
                position += channel.write(src, position).get();
            }
            return channel;
        }).given(serviceInstance()).write(notNull(), longThat(v -> v >= 0));
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, Executor) write(channel,
     * position, executor)} method returns a future whose result is same as given {@code channel},
     * and asserts {@link HelloWorld#write(AsynchronousFileChannel, long)} method invoked with
     * {@code channel} and {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("(channel, position, executor) -> channel.write(buffer, >= position)+")
    @Test
    void __()
            throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = _stub_ToWriteSome(mock(AsynchronousFileChannel.class), new LongAdder());
        var position = current().nextLong(0, Long.MAX_VALUE - BYTES);
        var executor = mock(Executor.class);
        willAnswer(i -> {
            var command = i.getArgument(0, Runnable.class);
            new Thread(command).start();
            return null;
        }).given(executor).execute(notNull());
        // ------------------------------------------------------------------------------------ when
        var future = service.writeAsync(channel, position, executor);
        var result = future.get();
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write(channel, position);
        assertSame(channel, result);
    }
}
