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

import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Executor)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithExecutor_Arguments_Test
 */
@Slf4j
class AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithExecutor_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Executor) write(channel,
     * executor)} method returns a future invokes
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel)} method.
     *
     * @throws InterruptedException if interrupted while running
     * @throws ExecutionException   if failed to execute
     */
    @DisplayName("write(channel, executor)"
                 + " returns a future invokes write(channel)")
    @Test
    void _InvokeWriteChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        when(service.write(channel)).thenReturn(channel);
        var executor = mock(Executor.class);
        lenient().
                doAnswer(i -> {
                    Runnable runnable = i.getArgument(0);
                    runnable.run();
                    return null;
                })
                .when(executor)
                .execute(any());
        // WHEN
        var future = service.write(channel, executor);
        var result = future.get();
        assertSame(channel, result);
        // THEN: write(channel) invoked once
    }
}
