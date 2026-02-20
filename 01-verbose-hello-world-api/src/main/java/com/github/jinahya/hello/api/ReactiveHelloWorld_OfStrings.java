package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;

non-sealed class ReactiveHelloWorld_OfStrings
        implements ReactiveHelloWorld.OfStrings {

    ReactiveHelloWorld_OfStrings(final ReactiveHelloWorld<byte[]> ofBytes) {
        super();
        this.ofArrays = Objects.requireNonNull(ofBytes, "ofBytes is null");
    }

    @Override
    public Subscription subscribe(final Subscriber<? super String> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        // TODO: now we can use ofBytes
        // TODO: in this class, use Locl/Condition instaand of object lock

        return null;
    }

    private final ReactiveHelloWorld<byte[]> ofArrays;
}
