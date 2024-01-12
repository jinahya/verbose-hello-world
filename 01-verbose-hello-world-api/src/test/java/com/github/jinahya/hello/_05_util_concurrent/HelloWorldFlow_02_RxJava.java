package com.github.jinahya.hello._05_util_concurrent;

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Executors;

@Slf4j
class HelloWorldFlow_02_RxJava extends _HelloWorldTest {

    @Test
    void __() {
        final var subscriber = new org.reactivestreams.Subscriber<Byte>() { // @formatter:off
            @Override public void onSubscribe(final org.reactivestreams.Subscription s) {
                log.debug("onSubscribe({})", s);
                assert s != null : "subscription supposed to be non-null";
                s.request(HelloWorld.BYTES);
            }
            @Override public void onNext(final Byte t) {
                log.debug("onNext({})", t);
            }
            @Override public void onError(final Throwable t) {
                log.error("onError({})", t, t);
            }
            @Override public void onComplete() {
                log.debug("onComplete()");
            }
        }; // @formatter:on
        final var executor = Executors.newSingleThreadExecutor(
                Thread.ofVirtual().name("byte-publisher-", 0L).factory()
        );
        HelloWorldFlow.newPublisherForBytes(service(), executor)
                .subscribe(FlowAdapters.toFlowSubscriber(subscriber));
    }
}
