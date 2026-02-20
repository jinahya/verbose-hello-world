package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

non-sealed class ReactiveHelloWorldDefaultOfBytes
        implements ReactiveHelloWorld.OfBytes {

    @Override
    public Subscription subscribe(Subscriber<? super Byte> subscriber) {
        return null;
    }
}
