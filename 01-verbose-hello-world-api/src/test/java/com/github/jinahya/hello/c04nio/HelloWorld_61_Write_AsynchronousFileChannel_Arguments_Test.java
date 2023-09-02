package com.github.jinahya.hello.c04nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.channels.AsynchronousFileChannel;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method regarding
 * arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_61_Write_AsynchronousFileChannel_Test
 */
@DisplayName("write(channel) arguments")
@Slf4j
class HelloWorld_61_Write_AsynchronousFileChannel_Arguments_Test extends _HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method throws a {@link NullPointerException} when the {@code channel} argument is
     * {@code null}.
     */
    @DisplayName("write(null, )NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        AsynchronousFileChannel channel = null;
        var position = 0L;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method throws a {@link IllegalArgumentException} when the {@code position} argument is
     * negative.
     */
    @DisplayName("write(, negative)IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() | Long.MIN_VALUE;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel, position)
        );
    }
}
