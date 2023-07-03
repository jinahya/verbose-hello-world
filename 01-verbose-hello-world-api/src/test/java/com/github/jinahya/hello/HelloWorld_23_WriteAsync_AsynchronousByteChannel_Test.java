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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousByteChannel) writeAsync(channel)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_23_WriteAsync_AsynchronousByteChannel_Arguments_Test
 */
@DisplayName("write(channel, handler)")
@Slf4j
class HelloWorld_23_WriteAsync_AsynchronousByteChannel_Test
        extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        stubPutBufferToReturnTheBufferAsItsPositionIncreasedBy12();
    }

    /**
     * Verifies {@link HelloWorld#writeAsync(AsynchronousByteChannel) writeAsync(channel)} method
     * returns a completable future being completed with the {@code channel}.
     */
    @DisplayName("(channel)completed<channel>")
    @Test
    void _Completed_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        stubToComplete(channel, writtenSoFar);
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.writeAsync(channel);
        // ------------------------------------------------------------------------------------ THEN
        assertNotNull(future);
        assertFalse(future.isCancelled());
        var result = future.get(8L, TimeUnit.SECONDS);
        // TODO: Get result of the future and verify it's the same as channel
        // TODO: Verify 12 bytes has been written to the channel
    }

    /**
     * Verify {@link HelloWorld#writeAsync(AsynchronousByteChannel) writeAsync(channel)} method
     * returns a completable future being completed exceptionally.
     */
    @DisplayName("(channel)completedExceptionally")
    @Test
    void _CompletedExceptionally_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        stubToFail(channel);
        // ------------------------------------------------------------------------------------ WHEN
        service.writeAsync(channel);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Verify handler.failed(notNull(), isNull()) invoked, once, in a handful seconds
    }
}
