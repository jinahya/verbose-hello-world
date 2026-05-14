package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Shared test fixtures for the {@code ReactiveHelloWorld*Publisher} tests — currently a family of
 * {@link Subscriber}s that log every signal ({@code onSubscribe}, {@code onNext}, {@code onError},
 * {@code onComplete}) at {@code DEBUG} level.
 * <p>
 * Concrete test classes are expected to {@link org.mockito.Mockito#spy(Object) spy} an
 * {@linkplain Subscriber} that {@linkplain Subscriber#onSubscribe(Subscription) onSubscribe}s with
 * a controlled request via an anonymous subclass:
 * <pre>{@code
 *     final var subscriber = Mockito.spy(new LoggingByteSubscriber() {
 *         @Override public void onSubscribe(final Subscription s) {
 *             super.onSubscribe(s);   // log
 *             s.request(n);           // test-specific demand
 *         }
 *     });
 * }</pre>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class ReactiveHelloWorld__Publisher__Tests {

    /**
     * A {@link Subscriber} that logs every received signal at {@code DEBUG} level.
     * <p>
     * {@link #onNext(Object) onNext} is intentionally an empty no-op in the base class — concrete
     * subclasses override it to add type-specific formatting.
     *
     * @param <T> the type of the element signaled to {@link #onNext(Object)}.
     */
    private static class LoggingSubscriber<T> implements Subscriber<T> {

        @Override
        public void onSubscribe(final Subscription s) {
            log.debug("onSubscribe({})", s);
        }

        @Override
        public void onNext(final T element) {
            // empty — subclasses override to log type-specific formatting
        }

        @Override
        public void onError(final Throwable t) {
            log.debug("onError({})", (Object) t);
        }

        @Override
        public void onComplete() {
            log.debug("onComplete()");
        }
    }

    /**
     * A {@link LoggingSubscriber} for {@link Byte} elements that formats each value as
     * {@code <hex>'<char>'} (e.g. {@code 68'h'}) in its {@link #onNext(Byte) onNext} log line.
     */
    static class LoggingByteSubscriber extends LoggingSubscriber<Byte> {

        @Override
        public void onNext(final Byte element) {
            log.debug("onNext({})", String.format("%02x'%c'", element, element));
        }
    }

    /**
     * A {@link LoggingSubscriber} for {@code byte[]} elements that formats each array as
     * {@code [<hex>'<char>' <hex>'<char>' ...]} in its {@link #onNext(byte[]) onNext} log line.
     */
    static class LoggingArraySubscriber extends LoggingSubscriber<byte[]> {

        @Override
        public void onNext(final byte[] element) {
            log.debug("onNext({})",
                      IntStream.range(0, element.length)
                              .mapToObj(i -> String.format("%02x'%c'", element[i], element[i]))
                              .collect(Collectors.joining(" ", "[", "]"))
            );
        }
    }

    /**
     * A {@link LoggingSubscriber} for {@link String} elements that logs each value double-quoted in
     * its {@link #onNext(String) onNext} log line.
     */
    static class LoggingStringSubscriber extends LoggingSubscriber<String> {

        @Override
        public void onNext(final String element) {
            log.debug("onNext(\"{}\")", element);
        }
    }

    private ReactiveHelloWorld__Publisher__Tests() {
        throw new AssertionError("instantiation is not allowed");
    }
}
