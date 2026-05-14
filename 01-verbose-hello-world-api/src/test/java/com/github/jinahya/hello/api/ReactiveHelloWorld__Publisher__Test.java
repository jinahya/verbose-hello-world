package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import java.util.Objects;
import java.util.function.Function;

@ExtendWith({MockitoExtension.class})
@Slf4j
abstract class ReactiveHelloWorld__Publisher__Test<T extends Publisher<U>, U> {

    ReactiveHelloWorld__Publisher__Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super();
        service = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        publisher = Mockito.spy(
                Objects.requireNonNull(
                        Objects.requireNonNull(initializer, "initializer is null").apply(service),
                        "null initialized"
                )
        );
    }

    // ---------------------------------------------------------------------------- java.lang.Object
    @Override
    public String toString() {
        return super.toString().substring(getClass().getPackageName().length() + 1);
    }

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final HelloWorld service;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PACKAGE)
    private final T publisher;
}
