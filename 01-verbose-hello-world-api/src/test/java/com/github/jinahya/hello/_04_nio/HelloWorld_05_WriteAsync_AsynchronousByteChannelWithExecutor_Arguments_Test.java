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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
 * @see HelloWorld_05_WriteAsync_AsynchronousByteChannelWithExecutor_Test
 */
@DisplayName("write(channel, executor) arguments")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_05_WriteAsync_AsynchronousByteChannelWithExecutor_Arguments_Test
        extends _HelloWorldTest {

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, Executor) write(channel, executor)}
     * method throws a {@link NullPointerException} when the {@code channel} argument is
     * {@code null}.
     */
    @DisplayName("[channel == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousByteChannel) null;
        final var executor = mock(Executor.class);
        // ------------------------------------------------------------------------------- when/then
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
    @DisplayName("[executor == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        var executor = (Executor) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, executor)
        );
    }
}
