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
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)}
 * method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_14_WriteAsync_AsynchronousFileChannel_Test
 */
@Slf4j
class HelloWorld_14_WriteAsync_AsynchronousFileChannel_Arguments_Test extends HelloWorldTest {

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)
     * writeAsync(channel, position, service)} method throws a {@link NullPointerException} when
     * {@code channel} argument is {@code null}.
     */
    @DisplayName("writeAsync(null, , ) throws NullPointerException")
    @Test
    void writeAsync_NullPointerException_ChannelIsNull() {
        var service = service();
        AsynchronousFileChannel channel = null;
        var position = current().nextLong() & Integer.MAX_VALUE;
        var executor = mock(ExecutorService.class);
        assertThrows(NullPointerException.class,
                     () -> service.writeAsync(channel, position, executor));
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)
     * writeAsync(channel, position, service)} method throws an {@link IllegalArgumentException}
     * when {@code position} argument is not positive.
     */
    @DisplayName("writeAsync(, negative, ) throws IllegalArgumentException")
    @Test
    void writeAsync_ThrowIllegalArgumentException_PositionIsNegative() {
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() | Integer.MIN_VALUE;
        var executor = mock(ExecutorService.class);
        assertThrows(IllegalArgumentException.class,
                     () -> service.writeAsync(channel, position, executor));
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService) writeAsync(channel,
     * position, service} method throws a {@link NullPointerException} when {@code channel} argument
     * is {@code null}.
     */
    @DisplayName("writeAsync(, , null) throws NullPointerException")
    @Test
    void writeAsync_ThrowNullPointerException_ServiceIsNull() {
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() & Integer.MAX_VALUE;
        ExecutorService executor = null;
        assertThrows(NullPointerException.class,
                     () -> service.writeAsync(channel, position, executor));
    }
}
