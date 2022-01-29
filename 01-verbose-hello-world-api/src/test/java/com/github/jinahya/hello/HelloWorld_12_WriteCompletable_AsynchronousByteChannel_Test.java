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
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing {@link HelloWorld#writeCompletable(AsynchronousByteChannel)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_12_WriteCompletable_AsynchronousByteChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_12_WriteCompletable_AsynchronousByteChannel_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeCompletable(AsynchronousByteChannel)
     * writeCompletable(channel)} method invokes {@link HelloWorld#put(ByteBuffer)
     * put(buffer)} method and writes the buffer to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeCompletable(channel) invokes put(buffer)"
                 + " and writes the buffer to channel")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        final LongAdder writtenSoFar = new LongAdder();
        final AsynchronousByteChannel channel = Mockito.mock(
                AsynchronousByteChannel.class);
        Mockito.lenient().doAnswer(i -> {
                    final ByteBuffer src = i.getArgument(0);
                    final Void attachment = i.getArgument(1);
                    final CompletionHandler<Integer, Void> handler
                            = i.getArgument(2);
                    final int written
                            = new Random().nextInt(src.remaining() + 1);
                    src.position(src.position() + written);
                    writtenSoFar.add(written);
                    handler.completed(written, attachment);
                    return null;
                })
                .when(channel)
                .write(Mockito.notNull(), // buffer
                       Mockito.any(),     // attachment
                       Mockito.notNull()  // handler
                );
        final CompletableFuture<AsynchronousByteChannel> future
                = helloWorld().writeCompletable(channel);
        final AsynchronousByteChannel actual = future.get();
        Assertions.assertSame(channel, actual);
        Mockito.verify(helloWorld(), Mockito.times(1))
                .put(bufferCaptor().capture());
        final ByteBuffer buffer = bufferCaptor().getValue();
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // TODO: Implement!
    }
}
