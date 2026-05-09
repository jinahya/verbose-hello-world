package com.github.jinahya.hello.api;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Utilities for testing {@link AsynchronousHelloWorld}.
 *
 * @see HelloWorldTestUtils
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
     * {@link AsynchronousHelloWorld#applyAsync(Function, Executor) applyAsync(mapper, executor)}
     * synchronously applies the {@code mapper} to the specified mock {@code service} and returns an
     * already-completed {@link CompletableFuture#completedStage(Object) completed} stage; if the
     * {@code mapper} throws, returns a {@link CompletableFuture#failedStage(Throwable) failed}
     * stage. The {@code executor} argument is ignored.
     *
     * @param service             the mock {@link HelloWorld} that backs the
     *                            {@code asynchronousService}.
     * @param asynchronousService the mock {@link AsynchronousHelloWorld} to stub.
     * @throws NullPointerException     if either argument is {@code null}.
     * @throws IllegalArgumentException if either argument is not a mock.
     */
    public static void applyAsync_mapper_executor_applies_(
            final HelloWorld service,
            final AsynchronousHelloWorld asynchronousService) {
        MockitoTestUtils.requireMock(service);
        MockitoTestUtils.requireMock(asynchronousService);
        Mockito.doAnswer(applies_(service))
                .when(asynchronousService)
                .applyAsync(
                        ArgumentMatchers.notNull(),
                        ArgumentMatchers.notNull()
                );
    }

    /**
     * Stubs the specified mock {@code asynchronousService} so that its
     * {@link AsynchronousHelloWorld#applyAsync(Function) applyAsync(mapper)} synchronously applies
     * the {@code mapper} to the specified mock {@code service} and returns an already-completed
     * {@link CompletableFuture#completedStage(Object) completed} stage; if the {@code mapper}
     * throws, returns a {@link CompletableFuture#failedStage(Throwable) failed} stage.
     * <p>
     * This overload is needed in addition to
     * {@link #applyAsync_mapper_executor_applies_(HelloWorld, AsynchronousHelloWorld)} because
     * Mockito spies do not intercept self-invocations: an implementation of
     * {@code applyAsync(mapper)} that internally delegates to {@code applyAsync(mapper, executor)}
     * bypasses the two-argument stub.
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
        MockitoTestUtils.requireMock(service);
        MockitoTestUtils.requireMock(asynchronousService);
        Mockito.doAnswer(applies_(service))
                .when(asynchronousService)
                .applyAsync(ArgumentMatchers.notNull());
    }

    private AsynchronousHelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
