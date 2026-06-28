package com.github.jinahya.hello.api._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object) write(channel, attachment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("AsynchronousHelloWorld.write(AsynchronousByteChannel, attachment)")
@Slf4j
abstract class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = (AsynchronousByteChannel) null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.write(channel, null));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes with
     * the supplied {@code attachment} once all {@value HelloWorld#BYTES} bytes have been written
     * across one or more partial writes.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("completed")
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        doAnswer(i -> {
            final var handler = i.getArgument(2, CompletionHandler.class);
            handler.completed(channel, attachment);
            return null;
        }).when(service).write(same(channel), same(attachment), notNull());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, attachment);
        // ------------------------------------------------------------------------------------ then
        assertSame(attachment, result.toCompletableFuture().get(8L, TimeUnit.SECONDS));
        verify(service, times(1)).write(same(channel), same(attachment), notNull());
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes
     * exceptionally when the {@code channel} fails — possibly synchronously on the first
     * invocation, or asynchronously after one or more partial writes.
     */
    @DisplayName("failed")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        doAnswer(i -> {
            final var handler = i.getArgument(2, CompletionHandler.class);
            handler.failed(exc, attachment);
            return null;
        }).when(service).write(same(channel), same(attachment), notNull());
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, attachment);
        // ------------------------------------------------------------------------------------ then
        final var thrown = assertThrows(
                ExecutionException.class,
                () -> result.toCompletableFuture().get(8L, TimeUnit.SECONDS)
        );
        assertSame(exc, thrown.getCause());
        verify(service, times(1)).write(same(channel), same(attachment), notNull());
    }
}
