package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public sealed interface ReactiveHelloWorld<T>
        permits ReactiveHelloWorld.OfBytes,
                ReactiveHelloWorld.OfArrays,
                ReactiveHelloWorld.OfStrings {

    sealed interface OfBytes
            extends ReactiveHelloWorld<Byte>
            permits ReactiveHelloWorl_OfBytes {

        static OfBytes newInstance(final HelloWorld service) {
            return new ReactiveHelloWorl_OfBytes(service);
        }

        static OfBytes newInstance() {
            return newInstance(HelloWorldRevisited.newInstance());
        }
    }

    sealed interface OfArrays
            extends ReactiveHelloWorld<byte[]>
            permits ReactiveHelloWorld_OfArrays {

        static OfArrays newInstance(final HelloWorld service) {
            return new ReactiveHelloWorld_OfArrays(OfBytes.newInstance(service));
        }

        static OfArrays newInstance() {
            return newInstance(HelloWorldRevisited.newInstance());
        }
    }

    sealed interface OfStrings
            extends ReactiveHelloWorld<String>
            permits ReactiveHelloWorld_OfStrings {

        static OfStrings newInstance(final HelloWorld service) {
            return new ReactiveHelloWorld_OfStrings(OfArrays.newInstance(service));
        }

        static OfStrings newInstance() {
            return newInstance(HelloWorldRevisited.newInstance());
        }
    }

    Subscription subscribe(Subscriber<? super T> subscriber);
}
