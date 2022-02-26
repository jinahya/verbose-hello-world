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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_10_Write_AsynchronousByteChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_10_Write_AsynchronousByteChannel_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method and writes the buffer to the {@code
     * channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel) invokes put(buffer)"
                 + " and writes the buffer to channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        final AsynchronousByteChannel channel = Mockito.mock(
                AsynchronousByteChannel.class);
        final LongAdder writtenSoFar = new LongAdder();
        Mockito.lenient().when(channel.write(ArgumentMatchers.notNull()))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final int written
                            = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    writtenSoFar.add(written);
                    @SuppressWarnings({"unchecked"})
                    final Future<Integer> future = Mockito.mock(Future.class);
                    Mockito.doReturn(written).when(future).get();
                    return future;
                });
        helloWorld().write(channel);
        Mockito.verify(helloWorld(), Mockito.times(1))
                .put(bufferCaptor().capture());
        final ByteBuffer buffer = bufferCaptor().getValue();
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method returns the
     * {@code channel} argument.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName(
            "write(channel) invokes put(buffer) writes the buffer to channel")
    @Test
    void write_ReturnChannel_()
            throws InterruptedException, ExecutionException {
        final AsynchronousByteChannel channel = Mockito.mock(
                AsynchronousByteChannel.class);
        Mockito.lenient()
                .when(channel.write(ArgumentMatchers.any(ByteBuffer.class)))
                .thenAnswer(
                        i -> {
                            final ByteBuffer buffer = i.getArgument(0);
                            final int written = new Random().nextInt(
                                    buffer.remaining() + 1);
                            buffer.position(buffer.position() + written);
                            return CompletableFuture.completedFuture(written);
                        });
        final AsynchronousByteChannel actual = helloWorld().write(channel);
        Assertions.assertSame(channel, actual);
    }
}
