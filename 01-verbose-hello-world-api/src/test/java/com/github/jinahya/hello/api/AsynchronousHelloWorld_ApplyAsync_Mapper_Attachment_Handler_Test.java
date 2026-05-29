package com.github.jinahya.hello.api;

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

import org.junit.jupiter.api.*;

import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

abstract class AsynchronousHelloWorld_ApplyAsync_Mapper_Attachment_Handler_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    // ---------------------------------------------------------------------------------------------
    AsynchronousHelloWorld_ApplyAsync_Mapper_Attachment_Handler_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    @Test
    @SuppressWarnings({"rawtypes"})
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final Function mapper = null;
        final var handler = mock(CompletionHandler.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class,
                     () -> asynchronousService.applyAsync(mapper, null, handler));
    }

    @Test
    @SuppressWarnings({"rawtypes"})
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var mapper = mock(Function.class);
        final CompletionHandler handler = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class,
                     () -> asynchronousService.applyAsync(mapper, null, handler));
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() {
        // ----------------------------------------------------------------------------------- given
        final var synchronousService = synchronousService();
        final var asynchronousService = asynchronousService();
        final var value = 42;
        final var mapper = mock(Function.class);
        when(mapper.apply(synchronousService)).thenReturn(value);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Integer, Object>) mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.applyAsync(mapper, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(value, attachment);
        verify(mapper, times(1)).apply(synchronousService);
        verify(handler, never()).failed(any(), any());
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        final var synchronousService = synchronousService();
        final var asynchronousService = asynchronousService();
        final var exc = new RuntimeException("simulated mapper failure");
        final var mapper = mock(Function.class);
        when(mapper.apply(synchronousService)).thenThrow(exc);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Object, Object>) mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.applyAsync(mapper, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        verify(handler, never()).completed(any(), any());
    }
}
