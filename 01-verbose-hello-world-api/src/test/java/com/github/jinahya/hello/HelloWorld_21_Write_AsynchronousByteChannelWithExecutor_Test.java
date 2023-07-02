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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.HelloWorldTestUtils.print;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousByteChannel, Executor) write(channel,executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_21_Write_AsynchronousByteChannelWithExecutor_Arguments_Test
 */
@DisplayName("write(channel, executor)")
@Slf4j
class HelloWorld_21_Write_AsynchronousByteChannelWithExecutor_Test
        extends _HelloWorldTest {

    /**
     * Stubs the {@link HelloWorld#put(ByteBuffer) put(buffer)} method to just return the
     * {@code buffer} as its {@code position} increased by {@value HelloWorld#BYTES}.
     */
    @DisplayName("[stubbing] put(buffer[12]) returns buffer as its position increased by 12")
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnBufferAsItsPositionIncreaseBy12_PutBuffer() {
        willAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            print(buffer);
            assert buffer.capacity() == BYTES;
            assert buffer.limit() == buffer.capacity();
            assert buffer.remaining() == BYTES;
            buffer.position(buffer.limit());
            return buffer;
        }).given(serviceInstance()).put(any());
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel, Executor) write(channel, executor)}
     * method writes {@value HelloWorld#BYTES} byte to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("channel.write(buffer)+")
    @Test
    void __() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        // channel.write(buffer) returns a future increments buffer's position by a random value
        var writtenSoFar = new LongAdder();
        given(channel.write((any()))).willAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            assert buffer.hasRemaining();
            var written = current().nextInt(1, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            given(future.get()).willReturn(written);
            return future;
        });
        var executor = mock(Executor.class);
        // executor.execute(runnable) starts a new thread runs the runnable
        willAnswer(i -> {
            Runnable runnable = i.getArgument(0);
            assert runnable != null;
            new Thread(runnable).start();
            return null;
        }).given(executor).execute(any());
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.write(channel, executor);
        var result = future.get();
        // ------------------------------------------------------------------------------------ THEN
        // THEN: once, write(channel) invoked
    }
}
