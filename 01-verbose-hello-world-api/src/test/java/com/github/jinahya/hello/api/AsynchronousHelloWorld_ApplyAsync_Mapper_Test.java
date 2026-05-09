package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AsynchronousHelloWorld_ApplyAsync_Mapper_Test
        extends AsynchronousHelloWorldTest {

    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var mapper = (Function<HelloWorld, Object>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(mapper)
        );
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var mapper = (Function<HelloWorld, Integer>) Mockito.mock(Function.class);
        final var stage = (CompletionStage<Integer>) Mockito.mock(CompletionStage.class);
        Mockito.doReturn(stage)
                .when(service).applyAsync(mapper, ForkJoinPool.commonPool());
        // ------------------------------------------------------------------------------------ when
        final var result = service.applyAsync(mapper);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1))
                .applyAsync(mapper, ForkJoinPool.commonPool());
        Assertions.assertSame(stage, result);
    }
}
