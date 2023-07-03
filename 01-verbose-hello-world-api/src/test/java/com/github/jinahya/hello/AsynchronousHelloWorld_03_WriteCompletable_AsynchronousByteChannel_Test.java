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
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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
        extends _AsynchronousHelloWorldTest {

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#writeCompletable(AsynchronousByteChannel)
     * writeCompletable(channel)} method invokes
     * {@link AsynchronousHelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler)
     * write(channel, handler)} method with specified {@code channel} and a {@code handler}, and
     * returns a completable future of specified {@code channel} which will be completed when the
     * {@code handler}'s {@link CompletionHandler#completed(Object, Object) completed(Integer, T)}
     * method is invoked with {@value HelloWorld#BYTES} and {@code channel}.
     *
     * @throws InterruptedException when interrupted getting a result from the future.
     * @throws ExecutionException   when the result future fails to execute.
     */
    @DisplayName("-> write(channel, handler <- completed)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = serviceInstance();
        doAnswer(i -> {
            AsynchronousByteChannel channel = i.getArgument(0);
            var handler = i.getArgument(1, CompletionHandler.class);
            new Thread(() -> handler.completed(BYTES, channel)).start();
            return null;
        }).when(service).write(notNull(), any(CompletionHandler.class), any());
        var channel = mock(AsynchronousByteChannel.class);
        // WHEN
        var future = service.writeCompletable(channel);
        // THEN: once, write(same(channel), any(CompletionHandler.class)) invoked
        AsynchronousByteChannel result;
        try {
            result = future.get(4L, SECONDS);
        } catch (TimeoutException te) {
            te.printStackTrace();
            return;
        }
        // THEN: result is same as channel
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#writeCompletable(AsynchronousByteChannel)
     * writeCompletable(channel)} method invokes
     * {@link AsynchronousHelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler)
     * write(channel, handler)} method with specified {@code channel} and a {@code handler}, and
     * returns a completable future of specified {@code channel} which will be
     * {@link CompletableFuture#completeExceptionally(Throwable) complted exceptionally} when the
     * {@code handler}'s {@link CompletionHandler#failed(Throwable, Object) failed(Throwable, T)}
     * method is invoked with an instance of {@link Throwable} and {@code channel}.
     *
     * @throws InterruptedException when interrupted getting a result from the future.
     */
    @DisplayName("-> write(channel, handler <- failed)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Failed_() throws InterruptedException {
        // GIVEN
        var service = serviceInstance();
        var exc = mock(Throwable.class);
        doAnswer(i -> {
            AsynchronousByteChannel channel = i.getArgument(0);
            var handler = i.getArgument(1, CompletionHandler.class);
            new Thread(() -> handler.failed(exc, channel)).start();
            return null;
        }).when(service).write(notNull(), any(CompletionHandler.class), any());
        var channel = mock(AsynchronousByteChannel.class);
        // WHEN
        var future = service.writeCompletable(channel);
        // THEN: once, write(same(channel), any(CompletionHandler.class)) invoked
        // THEN: future.get() throws an ExecutionException
        AsynchronousByteChannel result;
        try {
            result = future.get(4L, SECONDS);
        } catch (ExecutionException ee) {
            // TODO: Assert ee.getCause() is same as `exc`
        } catch (TimeoutException te) {
            te.printStackTrace();
        }
    }
}
