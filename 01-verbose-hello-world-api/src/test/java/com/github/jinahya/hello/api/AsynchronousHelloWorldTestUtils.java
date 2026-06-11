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

import org.mockito.*;
import org.mockito.stubbing.*;

import java.util.concurrent.*;
import java.util.function.*;

import static org.mockito.Mockito.*;

/**
 * Utilities for testing {@link AsynchronousHelloWorld}.
 *
 * @see HelloWorld__TestUtils
 */
public final class AsynchronousHelloWorldTestUtils {

    @SuppressWarnings({"unchecked"})
    private static Answer<CompletionStage<?>> applies_(final HelloWorld service) {
        return i -> {
            final Function<? super HelloWorld, ?> mapper = i.getArgument(0, Function.class);
            try {
                return CompletableFuture.completedStage(mapper.apply(service));
            } catch (final Throwable t) {
                return CompletableFuture.failedStage(t);
            }
        };
    }

    /**
     * Stubs the specified mock {@code asynchronousService} so that its
     * {@link AsynchronousHelloWorld#applyAsync(Function) applyAsync(mapper)} synchronously applies
     * the {@code mapper} to the specified mock {@code service} and returns an already-completed
     * {@link CompletableFuture#completedStage(Object) completed} stage; if the {@code mapper}
     * throws, returns a {@link CompletableFuture#failedStage(Throwable) failed} stage.
     *
     * @param service             the mock {@link HelloWorld} that backs the
     *                            {@code asynchronousService}.
     * @param asynchronousService the mock {@link AsynchronousHelloWorld} to stub.
     * @throws NullPointerException     if either argument is {@code null}.
     * @throws IllegalArgumentException if either argument is not a mock.
     */
    public static void applyAsync_mapper_applies_(
            final HelloWorld service,
            final AsynchronousHelloWorld asynchronousService) {
        Mockito__TestUtils.requireMock(service);
        Mockito__TestUtils.requireMock(asynchronousService);
        doAnswer(applies_(service))
                .when(asynchronousService)
                .applyAsync(ArgumentMatchers.notNull());
    }

    private AsynchronousHelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
