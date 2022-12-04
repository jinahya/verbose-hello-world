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

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long)} method
 * regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Test
 */
@Slf4j
class HelloWorld_15_WriteCompletable_AsynchronousFileChannel_Arguments_Test extends HelloWorldTest {

    /**
     * Asserts
     * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel,
     * position)} method throws a {@link NullPointerException} when the {@code channel} argument is
     * {@code null}.
     */
    @DisplayName("writeCompletable(null, ) throws NullPointerException")
    @Test
    void writeCompletable_ThrowNullPointerException_ChannelIsNull() {
        var service = helloWorld();
        AsynchronousFileChannel channel = null;
        var position = current().nextLong() & Long.MAX_VALUE;
        assertThrows(NullPointerException.class,
                     () -> service.writeCompletable(channel, position));
    }

    /**
     * Asserts
     * {@link HelloWorld#writeCompletable(AsynchronousFileChannel, long) writeCompletable(channel,
     * position)} method throws an {@link IllegalArgumentException} when the {@code position}
     * argument is negative.
     */
    @DisplayName("writeCompletable(, negative) throws IllegalArgumentException")
    @Test
    void writeCompletable_ThrowIllegalArgumentException_PositionIsNegative() {
        var service = helloWorld();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() | Long.MIN_VALUE;
        assertThrows(IllegalArgumentException.class,
                     () -> service.writeCompletable(channel, position));
    }
}
