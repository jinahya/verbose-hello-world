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
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#writeCompletable(AsynchronousByteChannel)
 * writeCompletable(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_03_WriteCompletable_AsynchronousByteChannel_Arguments_Test
 */
@DisplayName("writeCompletable(channel)")
@Slf4j
class AsynchronousHelloWorld_03_WriteCompletable_AsynchronousByteChannel_Test
        extends AsynchronousHelloWorldTest {

    //    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void stub__() {
        // GIVEN
        var service = service();
        // WHEN/THEN
        doAnswer(i -> {
            AsynchronousByteChannel channel = i.getArgument(0);
            var handler = i.getArgument(1, CompletionHandler.class);
            new Thread(() -> handler.completed(BYTES, null)).start();
            return null;
        }).when(service).write(any(), any(CompletionHandler.class));
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#writeCompletable(AsynchronousByteChannel)
     * writeCompletable(channel)} method invokes
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, CompletionHandler)
     * write(channel, handler)} method with a handler invokes either
     * {@link java.util.concurrent.CompletableFuture#complete(Object)} or
     * {@link java.util.concurrent.CompletableFuture#completeExceptionally(Throwable)}.
     */
    @DisplayName("write(channel, handler)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        doAnswer(i -> {
            AsynchronousByteChannel channel = i.getArgument(0);
            var handler = i.getArgument(1, CompletionHandler.class);
            new Thread(() -> handler.completed(BYTES, null)).start();
            return null;
        }).when(service).write(any(), any(CompletionHandler.class));
        var channel = mock(AsynchronousByteChannel.class);
        // WHEN
        var future = spy(service.writeCompletable(channel));
        // THEN: once, write(same(channel), handlerCaptor().capture()) invoked
        // TODO: Verify, once, write(same(channel), handlerCaptor().capture()) invoked
        AsynchronousByteChannel result;
        try {
            result = future.get(8L, SECONDS);
        } catch (TimeoutException te) {
            te.printStackTrace();
            return;
        }
        // TODO: Verify, once, future.complete(BYTES) invoked
        // TODO: Assert result is same as channel
    }
}
