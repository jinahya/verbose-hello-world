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

import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link ExecutorHelloWorld#applyAsync(Function) applyAsync(mapper)} method
 * directly with a same-thread ({@code Runnable::run}) executor.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("ExecutorHelloWorld.applyAsync(mapper)")
class ExecutorHelloWorld_ApplyAsync_Mapper_Test
        extends AsynchronousHelloWorld__Test<HelloWorld, ExecutorHelloWorld<HelloWorld>> {

    // ---------------------------------------------------------------------------------------------
    ExecutorHelloWorld_ApplyAsync_Mapper_Test() {
        super(HelloWorld.class, s -> new ExecutorHelloWorld<>(s, Runnable::run));
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code mapper}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / mapper is null")
    @Test
    @SuppressWarnings({"rawtypes"})
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final Function mapper = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> asynchronousService.applyAsync(mapper));
    }

    /**
     * Verifies that the method returns a
     * {@link java.util.concurrent.CompletionStage CompletionStage} that completes normally with the
     * value returned by {@code mapper.apply(service)}.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("happy path")
    @Test
    @SuppressWarnings({"unchecked"})
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var synchronousService = synchronousService();
        final var asynchronousService = asynchronousService();
        final var value = 42;
        final var mapper = mock(Function.class);
        doReturn(value).when(mapper).apply(synchronousService);
        // ------------------------------------------------------------------------------------ when
        final var result = asynchronousService.applyAsync(mapper);
        // ------------------------------------------------------------------------------------ then
        assertSame(value, result.toCompletableFuture().get());
        verify(mapper, times(1)).apply(synchronousService);
    }
}
