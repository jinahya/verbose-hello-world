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
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService)}
 * method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithExecutor_Test
 */
@DisplayName("write(channel, handler)")
@Slf4j
class AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithExecutor_Arguments_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Executor) write(channel,
     * executor)} method throws a {@link NullPointerException} when the {@code channel} argument is
     * {@code null}.
     */
    @DisplayName("write(null, executor) throws NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // GIVEN
        var service = service();
        AsynchronousByteChannel channel = null;
        var executor = mock(Executor.class);
        // WHEN/THEN
        assertThrows(NullPointerException.class, () -> service.write(channel, executor));
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Executor) write(channel,
     * executor)} method throws a {@link NullPointerException} when the {@code executor} argument is
     * {@code null}.
     */
    @DisplayName("write(channel, null) throws NullPointerException")
    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        Executor executor = null;
        // WHEN/THEN
        assertThrows(NullPointerException.class, () -> service.write(channel, executor));
    }
}
