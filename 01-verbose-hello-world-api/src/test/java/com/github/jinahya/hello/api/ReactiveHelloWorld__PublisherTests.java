package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;

@Slf4j
final class ReactiveHelloWorld__PublisherTests {

    static class BytePublisher implements org.reactivestreams.Publisher<Byte> {

        @Override
        public void subscribe(final Subscriber<? super Byte> s) {
        }
    }

    static class ArrayPublisher implements org.reactivestreams.Publisher<byte[]> {

        @Override
        public void subscribe(final Subscriber<? super byte[]> s) {
        }
    }

    static class StringPublisher implements org.reactivestreams.Publisher<String> {

        @Override
        public void subscribe(final Subscriber<? super String> s) {
        }
    }
}
