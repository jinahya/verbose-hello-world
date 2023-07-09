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
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeAsync(channel, position)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_33_WriteCompletable_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_33_WriteCompletable_AsynchronousFileChannel_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Verifies
     * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeAsync(channel,
     * position)} method returns a completable future being completed with the {@code channel}.
     *
     * @throws Exception when failed to ge the result of the future.
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
        assertNotNull(future);
        assertFalse(future.isCancelled());
        var result = future.get(8L, SECONDS);
        assertSame(channel, result);
        assertEquals(BYTES, writtenSoFar.intValue());
    }

    /**
     * Verifies {@link HelloWorld#writeCompletable(AsynchronousByteChannel) writeAsync(channel)}
     * method returns a completable future being completed exceptionally.
     */
    @DisplayName("(channel)completedExceptionally")
    @Test
    void _CompletedExceptionally_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        _stub_ToFail(channel);
        // ------------------------------------------------------------------------------------ WHEN
        var future = service.writeCompletable(channel);
        // ------------------------------------------------------------------------------------ THEN
        assertNotNull(future);
        assertFalse(future.isCancelled());
        assertThrows(CompletionException.class, future::join);
    }
}
