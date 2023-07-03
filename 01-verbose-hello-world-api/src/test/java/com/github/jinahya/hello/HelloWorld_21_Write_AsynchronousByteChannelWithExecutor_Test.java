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

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        stubPutBufferToIncreasePositionBy12();
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel, Executor) write(channel, executor)}
     * method returns a future whose result is same as given {@code channel}, and asserts
     * {@value HelloWorld#BYTES} bytes has been written to the {@code channel}.
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
        var writtenSoFar = new LongAdder();
        doAnswer(w -> {
            var future = mock(Future.class);
            when(future.get()).thenAnswer(g -> {
                ByteBuffer buffer = w.getArgument(0);
                assert buffer != null;
                assert buffer.hasRemaining();
                var written = current().nextInt(1, buffer.remaining() + 1);
                buffer.position(buffer.position() + written);
                writtenSoFar.add(written);
                return written;
            });
            return future;
        }).when(channel).write(any());
        var executor = mock(Executor.class);
        doAnswer(i -> {
            Runnable runnable = i.getArgument(0);
            assert runnable != null;
            new Thread(runnable).start();
            return null;
        }).when(executor).execute(any());
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.write(channel, executor);
        var result = future.get();
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Assert result is same as the channel
        // TODO: Assert 12 bytes were written to the channel
        // TODO: Assert put(buffer[12]) invoked, once
    }
}
