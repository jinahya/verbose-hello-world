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

import lombok.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.function.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AsynchronousHelloWorld_ApplyAsync_Mapper_Test
        extends AsynchronousHelloWorldTest {

    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var mapper = (Function<HelloWorld, Object>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.applyAsync(mapper)
        );
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var synchronousService = synchronousService();
        final var asynchronousService = asynchronousService();
        final var result = 42;
        final var mapper = (Function<HelloWorld, Integer>) Mockito.mock(Function.class);
        Mockito.when(mapper.apply(ArgumentMatchers.same(synchronousService))).thenReturn(result);
        // ------------------------------------------------------------------------------------ when
        final var stage = asynchronousService.applyAsync(mapper);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertSame(result, stage.toCompletableFuture().get());
        Mockito.verify(mapper, Mockito.times(1)).apply(ArgumentMatchers.same(synchronousService));
    }
}
