package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;

import java.util.function.Function;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingPublisher;
import static java.util.Objects.requireNonNull;
import static org.mockito.Mockito.mock;

/**
 * An abstract base for tests that verify subscription-level behaviour of the three concrete
 * {@code ReactiveHelloWorld<Type>Publisher} implementations against the Reactive Streams 1.0
 * contract — demand, completion, the single-terminal-signal rule (1.7), cancellation, …
 * <p>
 * The constructor builds fresh per-test state: a {@link Mockito#mock(Class) mock}
 * {@link HelloWorld} created with {@link Mockito#CALLS_REAL_METHODS CALLS_REAL_METHODS} (so the
 * {@code default} methods on the interface stay live), and the publisher produced by the
 * constructor-supplied {@code initializer} wrapped with
 * {@link HelloWorldBookUtils#loggingPublisher(Publisher) loggingPublisher} so every
 * {@code subscribe(...)} call is logged.
 * <p>
 * Concrete subclasses are expected to be named {@code ReactiveHelloWorldPublisher_<Type>_Test}.
 *
 * @param <U> the element type emitted by the publisher.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class ReactiveHelloWorldPublisher__Test<U> {

    ReactiveHelloWorldPublisher__Test(
            final Function<? super HelloWorld, ? extends Publisher<U>> initializer) {
        super();
        service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        publisher = loggingPublisher(requireNonNull(
                requireNonNull(initializer, "initializer is null").apply(service),
                "null initialized"
        ));
    }

    // ---------------------------------------------------------------------------- java.lang.Object
    @Override
    public String toString() {
        return HelloWorldBookUtils.toSimplifiedString(super.toString());
    }

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld service;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final Publisher<U> publisher;
}
