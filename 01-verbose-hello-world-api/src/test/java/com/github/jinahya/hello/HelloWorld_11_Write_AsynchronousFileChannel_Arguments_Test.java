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

import static java.lang.Long.MIN_VALUE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousFileChannel, long)} method regarding
 * arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_11_Write_AsynchronousFileChannel_Test
 */
@DisplayName("write(AsynchronousFileChannel, long) arguments")
@Slf4j
class HelloWorld_11_Write_AsynchronousFileChannel_Arguments_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method throws a {@link NullPointerException} when the {@code channel} argument is
     * {@code null}.
     */
    @DisplayName("[channel == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: AsynchronousByteChannel
        AsynchronousFileChannel channel = null;
        // GIVEN: position
        var position = 0L;
        // WHEN/THEN
        assertThrows(NullPointerException.class, () -> service.write(channel, position));
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method throws a {@link IllegalArgumentException} when the {@code position} argument is
     * negative.
     */
    @DisplayName("[position < 0L] -> IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: AsynchronousByteChannel
        var channel = mock(AsynchronousFileChannel.class);
        // GIVEN: position
        var position = current().nextLong() | MIN_VALUE;
        // WHEN/THEN
        assertThrows(IllegalArgumentException.class, () -> service.write(channel, position));
    }
}