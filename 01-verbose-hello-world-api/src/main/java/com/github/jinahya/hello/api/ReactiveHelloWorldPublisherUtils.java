package com.github.jinahya.hello.api;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Package-private utilities shared by the Reactive Streams publisher implementations
 * ({@link ReactiveHelloWorldBytePublisher}, {@link ReactiveHelloWorldArrayPublisher}, and
 * {@link ReactiveHelloWorldStringPublisher}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class ReactiveHelloWorldPublisherUtils {

    /**
     * Atomically adds the specified value to the specified demand counter, saturating at
     * {@link Long#MAX_VALUE} on overflow.
     *
     * @param demand the demand counter to update; must not be {@code null}.
     * @param n      the value to add; must be positive.
     * @return the updated demand value.
     */
    static long addDemand(final AtomicLong demand, final long n) {
        Objects.requireNonNull(demand, "demand is null");
        assert n > 0L;
        return demand.updateAndGet(v -> {
            try {
                return Math.addExact(v, n);
            } catch (final ArithmeticException _) {
                return Long.MAX_VALUE;
            }
        });
    }

    private ReactiveHelloWorldPublisherUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
