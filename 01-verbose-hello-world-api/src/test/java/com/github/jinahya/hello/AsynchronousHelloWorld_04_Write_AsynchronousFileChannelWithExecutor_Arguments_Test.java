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

import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.Executor;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
 * position, executor)} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_04_Write_AsynchronousFileChannelWithExecutor_Test
 */
@DisplayName("write(channel, position, executor) arguments")
@Slf4j
class AsynchronousHelloWorld_04_Write_AsynchronousFileChannelWithExecutor_Arguments_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
     * position, executor} method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("[channel == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // GIVEN
        var service = service();
        AsynchronousFileChannel channel = null;
        var position = current().nextLong() & MAX_VALUE;
        var executor = mock(Executor.class);
        // WHEN/THEN
        assertThrows(NullPointerException.class, () -> service.write(channel, position, executor));
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
     * position, executor)} method throws a {@link NullPointerException} when the {@code executor}
     * argument is {@code null}.
     */
    @DisplayName("[position < 0L] -> IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() | MIN_VALUE;
        var executor = mock(Executor.class);
        // WHEN/THEN
        assertThrows(IllegalArgumentException.class,
                     () -> service.write(channel, position, executor));
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
     * position, executor)} method throws a {@link NullPointerException} when the {@code executor}
     * argument is {@code null}.
     */
    @DisplayName("[executor == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() & MAX_VALUE;
        Executor executor = null;
        // WHEN/THEN
        assertThrows(NullPointerException.class,
                     () -> service.write(channel, position, executor));
    }
}
