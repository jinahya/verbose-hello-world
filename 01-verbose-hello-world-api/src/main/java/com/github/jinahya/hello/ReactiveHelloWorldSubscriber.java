package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
abstract class ReactiveHelloWorldSubscriber<T> implements Flow.Subscriber<T> {

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
    }

    @Override
    public void onNext(T item) {
        log.debug("item: {}", item);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("message: {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        log.debug("completed");
    }
}
