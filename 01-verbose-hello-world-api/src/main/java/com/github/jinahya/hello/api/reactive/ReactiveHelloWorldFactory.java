package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public interface ReactiveHelloWorldFactory {

    /**
     * Returns a publisher that publishes each byte of the hello-world-bytes.
     * <p>
     * The default implementation uses a naive publisher with an anonymous {@link HelloWorld}
     * implementation.
     *
     * @return a publisher that publishes each byte individually
     */
    default Publisher<Byte> newOctetPublisher() {
        return new ReactiveHelloWorldFactoryDefaults.DefaultOctetPublisher();
    }

    /**
     * Returns a publisher that publishes the hello-world-bytes as a single byte array.
     * <p>
     * The default implementation collects bytes from {@link #newOctetPublisher()} and publishes
     * them as a complete byte array.
     * <p>
     * This is a naive implementation: when a subscriber requests {@code n} items of {@code byte[]},
     * for each item, the implementation subscribes to the bytes publisher, requests 12 bytes, waits
     * for completion, and then publishes the collected array.
     * <p>
     * Cancellation is handled by checking a volatile boolean flag in the request loop. When
     * {@link Subscription#cancel()} is invoked, no new subscriptions to the bytes publisher are
     * created. However, in-flight subscriptions may still complete and emit items, which is allowed
     * by the Reactive Streams specification.
     *
     * @return a publisher that publishes the hello-world-bytes as a byte array
     */
    default Publisher<byte[]> newArrayPublisher() {
        return s -> s.onSubscribe(
                new ReactiveHelloWorldFactoryDefaults.ArraySubscription(
                        s,
                        newOctetPublisher()
                )
        );
    }

    /**
     * Returns a publisher that publishes the string representation of the hello-world-bytes.
     * <p>
     * The default implementation collects byte arrays from {@link #newArrayPublisher()} and
     * converts them to strings.
     *
     * @return a publisher that publishes the hello-world-bytes as a string
     */
    default Publisher<String> newStringPublisher() {
        return s -> s.onSubscribe(
                new ReactiveHelloWorldFactoryDefaults.StringSubscription(
                        s,
                        newArrayPublisher()
                )
        );
    }
}
