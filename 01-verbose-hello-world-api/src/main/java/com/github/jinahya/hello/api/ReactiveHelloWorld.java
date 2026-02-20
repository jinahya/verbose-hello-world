package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public sealed interface ReactiveHelloWorld<T>
        permits ReactiveHelloWorld.OfBytes {

    sealed interface OfBytes
            extends ReactiveHelloWorld<Byte>
            permits ReactiveHelloWorldDefaultOfBytes {

    }

    Subscription subscribe(Subscriber<? super T> subscriber);
}
