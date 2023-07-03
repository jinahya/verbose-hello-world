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
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousByteChannel, Executor) writeAsync(channel, executor)}
 * method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_21_WriteAsync_AsynchronousByteChannelWithExecutor_Test
 */
@DisplayName("write(channel, executor) arguments")
@Slf4j
class HelloWorld_21_WriteAsync_AsynchronousByteChannelWithExecutor_Arguments_Test
        extends _HelloWorldTest {

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, Executor) write(channel, executor)}
     * method throws a {@link NullPointerException} when the {@code channel} argument is
     * {@code null}.
     */
    @DisplayName("(null, )NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = (AsynchronousByteChannel) null;
        var executor = mock(Executor.class);
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, executor)
        );
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, Executor) write(channel, executor)}
     * method throws a {@link NullPointerException} when the {@code executor} argument is
     * {@code null}.
     */
    @DisplayName("(, null)NullPointerException")
    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var executor = (Executor) null;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, executor)
        );
    }
}
