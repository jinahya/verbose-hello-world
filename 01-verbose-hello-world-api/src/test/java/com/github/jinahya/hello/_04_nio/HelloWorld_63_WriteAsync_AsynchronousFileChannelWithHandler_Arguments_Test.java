package com.github.jinahya.hello._04_nio;

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
import java.nio.channels.CompletionHandler;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
 * write(channel, position, hanldler, attachment)} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_63_WriteAsync_AsynchronousFileChannelWithHandler_Test
 */
@DisplayName("write(channel, position, handler, attachment) arguments")
@Slf4j
class HelloWorld_63_WriteAsync_AsynchronousFileChannelWithHandler_Arguments_Test
        extends _HelloWorldTest {

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method throws a {@link NullPointerException}
     * when the {@code channel} argument is {@code null}.
     */
    @DisplayName("(null, , , )NullPointerException")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = (AsynchronousFileChannel) null;
        var position = 0L;
        var handler = mock(CompletionHandler.class);
        var attachment = (Object) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, position, handler, attachment)
        );
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method throws an
     * {@link IllegalArgumentException} when the {@code position} argument is negative.
     */
    @DisplayName("(, < 0, , )IllegalArgumentException")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var position = current().nextLong() | Long.MIN_VALUE;
        var handler = mock(CompletionHandler.class);
        var attachment = (Object) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                IllegalArgumentException.class,
                () -> service.writeAsync(channel, position, handler, attachment)
        );
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method throws a {@link NullPointerException}
     * when the {@code handler} argument is {@code null}.
     */
    @DisplayName("(, , null, )NullPointerException")
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var position = 0L;
        var handler = (CompletionHandler<AsynchronousFileChannel, Object>) null;
        var attachment = (Object) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, position, handler, attachment)
        );
    }
}
