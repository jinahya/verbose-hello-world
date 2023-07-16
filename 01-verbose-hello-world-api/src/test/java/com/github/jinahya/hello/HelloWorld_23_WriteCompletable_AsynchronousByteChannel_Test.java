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

import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeCompletable(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_23_WriteCompletable_AsynchronousByteChannel_Arguments_Test
 */
@DisplayName("writeCompletable(channel)")
@Slf4j
class HelloWorld_23_WriteCompletable_AsynchronousByteChannel_Test
        extends _HelloWorldTest {

    @BeforeEach
    @SuppressWarnings({
            "unchecked" // handler.completed
    })
    void _beforeEach() {
        willAnswer(i -> {
            var channel = i.getArgument(0, AsynchronousByteChannel.class);
            var handler = i.getArgument(1, CompletionHandler.class);
            var attachment = i.getArgument(2);
            for (final var b = allocate(BYTES); b.hasRemaining(); channel.write(b).get()) {
                // empty
            }
            handler.completed(channel, attachment);
            return null;
        }).given(serviceInstance()).writeAsync(notNull(), notNull(), any());
    }

    /**
     * Verifies
     * {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeCompletable(channel)} method
     * returns a completable future completes with the {@code channel}.
     *
     * @throws Exception if thrown while getting the result of the future returned from the
     *                   {@link HelloWorld#writeCompletable(AsynchronousByteChannel)
     *                   writeCompletable(channel)} method.
     * @see CompletableFuture#get(long, TimeUnit)
     */
    @DisplayName("(channel)completed<channel>")
    @Test
    void _Completed_() throws Exception {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        _stub_ToComplete(channel, writtenSoFar);
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.writeCompletable(channel);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Get the result of the <future> with a timeout.
        // TODO: Verify, service.writeAsync(chanel, a-handler, null) invoked, once.
        // TODO: Assert, result is same as channel.
        // TODO: Assert, writtenSoFar.intValue() is equal to BYTES.
    }

    /**
     * Verifies
     * {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeCompletable(channel)} method
     * returns a completable future being completed exceptionally.
     *
     * @see CompletableFuture#handle(BiFunction)
     * @see CompletableFuture#join()
     */
    @DisplayName("(channel)completedExceptionally")
    @Test
    void _CompletedExceptionally_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var exc = _stub_ToFail(channel, mock(Throwable.class));
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.writeCompletable(channel);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Join the result of the <future> handling to return what has been thrown
        // TODO: Assert, the thrown is same as <exc>
    }
}
