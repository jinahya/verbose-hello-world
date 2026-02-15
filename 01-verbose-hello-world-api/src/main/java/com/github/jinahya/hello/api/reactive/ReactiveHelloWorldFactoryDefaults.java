package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Default implementations for {@link ReactiveHelloWorldFactory}.
 * <p>
 * This class provides naive, educational implementations of reactive publishers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class ReactiveHelloWorldFactoryDefaults {

    /**
     * The hello-world bytes.
     */
    private static final byte[] BYTES = "hello, world".getBytes(StandardCharsets.US_ASCII);

    /**
     * Private anonymous implementation of {@link HelloWorld}.
     */
    private static final HelloWorld SERVICE = (a, i) -> {
        System.arraycopy(BYTES, 0, a, i, BYTES.length);
        return a;
    };

    // ---------------------------------------------------------------------------------------------
    abstract static class AbstractSubscriber<T>
            implements Subscriber<T> {

        AbstractSubscriber(final long n) {
            super();
            this.n = n;
        }

        @Override
        public final void onSubscribe(final Subscription s) {
            s.request(n);
        }

        @Override
        public void onNext(final T t) {
        }

        @Override
        public void onError(final Throwable t) {
        }

        @Override
        public void onComplete() {
        }

        private final long n;
    }

    // ---------------------------------------------------------------------------------------------
    abstract static class AbstractSubscription
            implements Subscription {

        @Override
        public void cancel() {
            cancelled = true;
        }

        volatile boolean cancelled = false;

        volatile boolean terminated = false;
    }

    static class ByteSubscription
            extends AbstractSubscription {

        ByteSubscription(final Subscriber<? super Byte> subscriber) {
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
        }

        @Override
        public void request(final long n) {
            if (terminated || cancelled) {
                return;
            }
            if (array == null) {
                array = SERVICE.set(new byte[HelloWorld.BYTES]);
                index = 0;
            }
            for (var i = 0; i < n && index < array.length && !cancelled; i++) {
                subscriber.onNext(array[index++]);
            }
            if (index == array.length && !cancelled) {
                terminated = true; // Set before onComplete() per Rule 1.6
                subscriber.onComplete();
            }
        }

        private final Subscriber<? super Byte> subscriber;

        private byte[] array;

        private int index;
    }

    static class ArraySubscription
            extends AbstractSubscription {

        private final Subscriber<? super byte[]> subscriber;

        private final Publisher<Byte> publisher;

        ArraySubscription(final Subscriber<? super byte[]> subscriber,
                          final Publisher<Byte> publisher) {
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
            this.publisher = Objects.requireNonNull(publisher, "publisher is null");
        }

        @Override
        public void request(final long n) {
            if (terminated) {
                return;
            }
            if (cancelled) {
                return;
            }
            for (int i = 0; i < n && !cancelled; i++) {
                publisher.subscribe(new AbstractSubscriber<Byte>(HelloWorld.BYTES) { // @formatter:off
                    final byte[] array = new byte[HelloWorld.BYTES];
                    int index = 0;
                    @Override public void onNext(final Byte item) {
                        array[index++] = item;
                    }
                    @Override public void onError(final Throwable throwable) {
                        if (!terminated) {
                            terminated = true;
                            subscriber.onError(throwable);
                        }
                    }
                    @Override public void onComplete() {
                        if (!terminated) {
                            terminated = true;
                            subscriber.onNext(array);
                            subscriber.onComplete();
                        }
                    } // @formatter:on
                });
            }
        }
    }

    static class StringSubscription
            extends AbstractSubscription {

        StringSubscription(final Subscriber<? super String> subscriber,
                           final Publisher<byte[]> publisher) {
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
            this.publisher = Objects.requireNonNull(publisher, "publisher is null");
        }

        @Override
        public void request(final long n) {
            if (terminated) {
                return;
            }
            if (cancelled) {
                return;
            }
            for (int i = 0; i < n && !cancelled; i++) {
                publisher.subscribe(new AbstractSubscriber<byte[]>(1) { // @formatter:off
                    @Override public void onNext(final byte[] array) {
                        final var string = new String(array, StandardCharsets.US_ASCII);
                        if (!terminated) {
                            terminated = true;
                            subscriber.onNext(string);
                            subscriber.onComplete();
                        }
                    }
                    @Override public void onError(final Throwable throwable) {
                        if (!terminated) {
                            terminated = true;
                            subscriber.onError(throwable);
                        }
                    }
                    @Override public void onComplete() {
                    } // @formatter:on
                });
            }
        }

        private final Subscriber<? super String> subscriber;

        private final Publisher<byte[]> publisher;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * A naive implementation of a {@link Publisher} that publishes each byte of the
     * hello-world-bytes.
     * <p>
     * This class demonstrates the basic reactive streams pattern without complex backpressure
     * handling.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    static final class DefaultOctetPublisher
            implements Publisher<Byte> {

        /**
         * Creates a new instance.
         */
        DefaultOctetPublisher() {
            super();
        }

        @Override
        public void subscribe(final Subscriber<? super Byte> subscriber) {
            Objects.requireNonNull(subscriber, "subscriber is null");
            subscriber.onSubscribe(new ByteSubscription(subscriber));
        }
    }

    // ---------------------------------------------------------------------------------------------
    private ReactiveHelloWorldFactoryDefaults() {
        throw new AssertionError("instantiation is not allowed");
    }
}
