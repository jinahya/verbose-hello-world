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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.AsynchronousFileChannel;
import java.util.Random;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousFileChannel, long)} method regarding
 * arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_13_Write_AsynchronousFileChannel_Test
 */
@Slf4j
class HelloWorld_13_Write_AsynchronousFileChannel_Arguments_Test
        extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method throws a {@link NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("write(null, ) throws NullPointerException")
    @Test
    void write_ThrowNullPointerException_ChannelIsNull() {
        var service = helloWorld();
        AsynchronousFileChannel channel = null;
        var position = new Random().nextLong() & Long.MAX_VALUE;
        Assertions.assertThrows(NullPointerException.class, () -> service.write(channel, position));
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method throws a {@link IllegalArgumentException} when the {@code position} argument is
     * negative.
     */
    @DisplayName("write(, negative) throws IllegalArgumentException")
    @Test
    void write_ThrowIllegalArgumentException_PositionIsNegative() {
        var service = helloWorld();
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        var position = new Random().nextLong() | Long.MIN_VALUE;
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> service.write(channel, position));
    }
}
