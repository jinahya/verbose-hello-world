package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AsynchronousHelloWorld_ApplyAsync_Target_Mapper_Test
        extends AsynchronousHelloWorldTest {

    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var target = (Object) null;
        final var mapper = Mockito.mock(BiFunction.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(target, mapper)
        );
    }

    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var target = new Object();
        final var mapper = (BiFunction<HelloWorld, Object, Object>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(target, mapper)
        );
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var target = "hello";
        final var mapper = (BiFunction<HelloWorld, String, Integer>) Mockito.mock(BiFunction.class);
        final var stage = (CompletionStage<Integer>) Mockito.mock(CompletionStage.class);
        Mockito.doReturn(stage)
                .when(service).applyAsync(target, mapper, ForkJoinPool.commonPool());
        // ------------------------------------------------------------------------------------ when
        final var result = service.applyAsync(target, mapper);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1))
                .applyAsync(target, mapper, ForkJoinPool.commonPool());
        Assertions.assertSame(stage, result);
    }
}
