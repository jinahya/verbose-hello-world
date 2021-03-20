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
import org.mockito.ArgumentCaptor;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#writeSync(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_WriteSyncAsynchronousByteChannelTest extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel)} method throws a {@link NullPointerException} when
     * {@code channel} argument is {@code null}.
     */
    @DisplayName("writeSync(channel) throws NullPointerException when channel is null")
    @Test
    void writeSync_NullPointerException_ChannelIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.writeSync((AsynchronousByteChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#writeSync(AsynchronousByteChannel)} invokes {@link HelloWorld#put(ByteBuffer)} and
     * writes the buffer to {@code channel}.
     *
     * @throws ExecutionException   if failed to work.
     * @throws InterruptedException if interrupted while executing.
     */
    @DisplayName("writeSync(channel) invokes put(buffer) writes the buffer to channel")
    @Test
    void writeSync_InvokePutBufferWriteBufferToChannel_() throws ExecutionException, InterruptedException {
        final AsynchronousByteChannel channel = mock(AsynchronousByteChannel.class);
        final LongAdder adder = new LongAdder();
        when(channel.write(any(ByteBuffer.class))).thenAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            final int written = current().nextInt(0, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            adder.add(written);
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        helloWorld.writeSync(channel);
        final ArgumentCaptor<ByteBuffer> bufferCaptor1 = ArgumentCaptor.forClass(ByteBuffer.class);
        verify(helloWorld, times(1)).put(bufferCaptor1.capture());
        final ByteBuffer buffer1 = bufferCaptor1.getValue();
    }

    /**
     * Asserts {@link HelloWorld#writeSync(AsynchronousByteChannel)} return given {@code channel}.
     *
     * @throws ExecutionException   if failed to work.
     * @throws InterruptedException if interrupted while executing.
     */
    @DisplayName("writeSync(channel) returns channel")
    @Test
    void writeSync_ReturnChannel_() throws ExecutionException, InterruptedException {
        final AsynchronousByteChannel expected = mock(AsynchronousByteChannel.class);
        final AsynchronousByteChannel actual = helloWorld.writeSync(expected);
        assertSame(expected, actual);
    }
}
