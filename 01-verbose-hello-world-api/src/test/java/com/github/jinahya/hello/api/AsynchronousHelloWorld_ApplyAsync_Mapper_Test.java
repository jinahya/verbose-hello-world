package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.function.Function;

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
