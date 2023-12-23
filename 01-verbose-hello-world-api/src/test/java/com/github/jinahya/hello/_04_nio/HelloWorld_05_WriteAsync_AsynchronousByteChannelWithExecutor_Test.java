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
import com.github.jinahya.hello._HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousByteChannel, Executor) writeAsync(channel,executor)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_WriteAsync_AsynchronousByteChannelWithExecutor_Arguments_Test
 */
@DisplayName("write(channel, executor)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_05_WriteAsync_AsynchronousByteChannelWithExecutor_Test
        extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() throws ExecutionException, InterruptedException {
        doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            for (final var b = ByteBuffer.allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                final var w = channel.write(b).get();
                assert w >= 0;
            }
            return channel;
        }).when(service()).write(notNull(AsynchronousByteChannel.class));
    }

    /**
     * Verifies
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, Executor) write(channel, executor)}
     * method returns a future of {@code channel}, and asserts {@value HelloWorld#BYTES} bytes has
     * been written to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("-> put(buffer[12]) -> channel.write(buffer)+")
    @Test
    void __() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(AsynchronousByteChannel.class);
        final var writtenSoFar = new LongAdder();
        _HelloWorldTestUtils.writeBuffer_willWriteSome(channel, writtenSoFar);
        final var executor = mock(Executor.class);
        doAnswer(i -> {
            final var command = i.getArgument(0, Runnable.class);
            new Thread(command).start();
            return null;
        }).when(executor).execute(notNull());
        // ------------------------------------------------------------------------------------ when
        final var future = service.writeAsync(channel, executor);
        final var result = future.get();
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write(channel);
        assertSame(channel, result);
    }
}
