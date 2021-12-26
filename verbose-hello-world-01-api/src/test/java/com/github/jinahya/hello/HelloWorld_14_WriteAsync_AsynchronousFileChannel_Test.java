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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_14_WriteAsync_AsynchronousFileChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_14_WriteAsync_AsynchronousFileChannel_Test extends HelloWorldTest {

    // TODO: Remove this stubbing method when you implemented the put(buffer) method!
    @BeforeEach
    void beforeEach() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        Mockito.doAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(helloWorld())
                .put(ArgumentMatchers.notNull());
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)
     * writeAsync(channel, posotion, service)} method returns a future of {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeAsync(channel, position, service) returns Future(channel)")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        final LongAdder writtenSoFar = new LongAdder();
        final AsynchronousFileChannel channel = Mockito.mock(AsynchronousFileChannel.class);
        Mockito.when(channel.write(ArgumentMatchers.notNull(),
                                   ArgumentMatchers.longThat(a -> a >= 0L)))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final long position = i.getArgument(1);
                    final int written = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    writtenSoFar.add(written);
                    @SuppressWarnings({"unchecked"})
                    final Future<Integer> future = Mockito.mock(Future.class);
                    Mockito.doReturn(written).when(future).get();
                    return future;
                });
        final long position = 0L;
        final ExecutorService service = Executors.newSingleThreadExecutor();
        final Future<AsynchronousFileChannel> future
                = helloWorld().writeAsync(channel, position, service);
        final AsynchronousFileChannel actual = future.get();
        Assertions.assertSame(channel, actual);
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue());
    }
}
