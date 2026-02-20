package com.github.jinahya.hello.api;

import org.reactivestreams.Subscription;

import java.util.concurrent.Flow;

interface ReactiveHelloWorld<T> {

    Subscription subscribe(Flow.Subscriber<? super T> subscriber);
}
