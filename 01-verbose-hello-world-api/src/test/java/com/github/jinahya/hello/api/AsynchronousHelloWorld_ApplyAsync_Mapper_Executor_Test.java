package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.concurrent.Executor;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class AsynchronousHelloWorld_ApplyAsync_Mapper_Executor_Test
        extends AsynchronousHelloWorldTest {

    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var mapper = (Function<HelloWorld, Object>) null;
        final var executor = Mockito.mock(Executor.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(mapper, executor)
        );
    }

    @Test
    void _ThrowNullPointerException_ExecutorIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var mapper = Mockito.mock(Function.class);
        final var executor = (Executor) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.applyAsync(mapper, executor)
        );
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var result = 42;
        final var mapper = (Function<HelloWorld, Integer>) Mockito.mock(Function.class);
        Mockito.when(mapper.apply(ArgumentMatchers.notNull())).thenReturn(result);
        final var executor = Mockito.mock(Executor.class);
        Mockito.doAnswer(i -> {
            i.getArgument(0, Runnable.class).run();
            return null;
        }).when(executor).execute(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------------ when
        final var stage = service.applyAsync(mapper, executor);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertSame(result, stage.toCompletableFuture().get());
        Mockito.verify(executor, Mockito.times(1)).execute(ArgumentMatchers.notNull());
        Mockito.verify(mapper, Mockito.times(1))
                .apply(ArgumentMatchers.notNull());
    }
}
