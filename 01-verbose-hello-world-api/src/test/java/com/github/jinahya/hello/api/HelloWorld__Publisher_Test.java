package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.util.concurrent.Flow;
import java.util.function.Function;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingPublisher;
import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.mock;

/**
 * An abstract base for tests that verify subscription-level behaviour of the three concrete
 * {@code HelloWorld<Type>Publisher} {@link Flow.Publisher} implementations against the
 * {@link java.util.concurrent.Flow} contract.
 * <p>
 * The constructor builds fresh per-test state: a {@link Mockito#mock(Class) mock}
 * {@link HelloWorld} created with {@link Mockito#CALLS_REAL_METHODS CALLS_REAL_METHODS}, and the
 * publisher produced by the {@code initializer} wrapped with
 * {@link HelloWorldBookUtils#loggingPublisher(Flow.Publisher) loggingPublisher} so every
 * {@code subscribe(...)} call is logged. The raw (un-proxied) {@code delegate} is also retained so
 * that {@link AutoCloseable} cleanup can run after each test.
 *
 * @param <U> the element type emitted by the publisher.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class HelloWorld__Publisher_Test<U> {

    HelloWorld__Publisher_Test(
            final Function<? super HelloWorld, ? extends Flow.Publisher<U>> initializer) {
        super();
        service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        delegate = requireNonNull(
                requireNonNull(initializer, "initializer is null").apply(service),
                "null initialized"
        );
        publisher = loggingPublisher(delegate);
    }

    // ---------------------------------------------------------------------------------------------
    @AfterEach
    void closeIfCloseable() throws Exception {
        if (delegate instanceof AutoCloseable closeable) {
            closeable.close();
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld service;

    private final Flow.Publisher<U> delegate;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final Flow.Publisher<U> publisher;
}
