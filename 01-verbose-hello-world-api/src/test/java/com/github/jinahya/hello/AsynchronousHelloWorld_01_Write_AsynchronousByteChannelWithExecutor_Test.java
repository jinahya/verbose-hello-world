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
import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link AsynchronousHelloWorld#write(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_01_Write_AsynchronousByteChannelWithExecutor_Arguments_Test
 */
@DisplayName("write(AsynchronousByteChannel, Executor) arguments")
@Slf4j
class AsynchronousHelloWorld_01_Write_AsynchronousByteChannelWithExecutor_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer) put(buffer)} method to return the {@code buffer}
     * argument with its {@link ByteBuffer#position() position} increased by
     * {@value HelloWorld#BYTES}.
     */
    @BeforeEach
    void stub_PutBuffer_IncreaseBufferPositionBy12() {
        lenient().doAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + BYTES);
                    return buffer;
                })
                .when(service())
                .put(argThat(b -> b != null && b.remaining() >= BYTES));
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Executor) write(channel,
     * executor)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer
     * of {@value HelloWorld#BYTES} bytes, writes the buffer to the {@code channel}, and returns a
     * future of {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("-> put(buffer[12])"
                 + " -> channel.write(buffer)+"
                 + " -> future<channel>")
    @Test
    void __() throws InterruptedException, ExecutionException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: AsynchronousByteChannel
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        lenient().when(channel.write(argThat(b -> b != null && b.hasRemaining()))).thenAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            var written = current().nextInt(buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            doReturn(written).when(future).get();
            return future;
        });
        // GIVEN: Executor
        var executor = mock(Executor.class);
        lenient().doAnswer(i -> {
            var runnable = i.getArgument(0, Runnable.class);
            new Thread(runnable).start();
            return null;
        }).when(executor).execute(notNull());
        // WHEN
        var future = service.write(channel, executor);
        var result = future.get();
        // THEN: put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: at least once, channel.write(buffer) invoked
        // THEN: 12 bytes are written
        // THEN: result is same as channel
    }
}
