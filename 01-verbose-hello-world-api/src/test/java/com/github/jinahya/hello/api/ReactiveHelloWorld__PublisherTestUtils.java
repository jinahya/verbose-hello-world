package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities shared by {@link ReactiveHelloWorld__PublisherTest} and its concrete subclasses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class ReactiveHelloWorld__PublisherTestUtils {

    /**
     * Sleeps the current thread for the specified positive {@link Duration}.
     * <p>
     * If the current thread is interrupted while sleeping, the interrupt status is restored and an
     * unchecked {@link RuntimeException} wrapping the {@link InterruptedException} is thrown.
     *
     * @param duration the sleep duration; must not be {@code null} and must be positive (i.e.,
     *                 neither {@linkplain Duration#isZero() zero} nor
     *                 {@linkplain Duration#isNegative() negative}).
     * @throws NullPointerException     when the {@code duration} is {@code null}.
     * @throws IllegalArgumentException when the {@code duration} is not positive.
     * @throws RuntimeException         when the current thread is interrupted while sleeping; the
     *                                  cause is the original {@link InterruptedException}.
     */
    static void sleep(final Duration duration) {
        Objects.requireNonNull(duration, "duration is null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("duration is not positive: " + duration);
        }
        try {
            Thread.sleep(duration);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }

    /**
     * Sleeps the current thread for a random number of seconds drawn from the specified range.
     * <p>
     * The number of seconds is uniformly chosen from
     * {@code [minimumSecondsInclusive, maximumSecondsExclusive)}.
     *
     * @param minimumSecondsInclusive the (inclusive) lower bound, in seconds; must be positive.
     * @param maximumSecondsExclusive the (exclusive) upper bound, in seconds; must be strictly
     *                                greater than {@code minimumSecondsInclusive}.
     * @throws IllegalArgumentException when {@code minimumSecondsInclusive} is not positive, or
     *                                  when {@code maximumSecondsExclusive} is not greater than
     *                                  {@code minimumSecondsInclusive}.
     * @throws RuntimeException         when the current thread is interrupted while sleeping; the
     *                                  cause is the original {@link InterruptedException}.
     * @see #sleep(Duration)
     */
    static void sleep(final long minimumSecondsInclusive, final long maximumSecondsExclusive) {
        if (minimumSecondsInclusive <= 0L) {
            throw new IllegalArgumentException(
                    "minimumSecondsInclusive(" + minimumSecondsInclusive + ") is not positive");
        }
        if (maximumSecondsExclusive <= minimumSecondsInclusive) {
            throw new IllegalArgumentException(
                    "maximumSecondsExclusive(" + maximumSecondsExclusive
                    + ") <= minimumSecondsInclusive(" + minimumSecondsInclusive + ")");
        }
        final var seconds = ThreadLocalRandom.current().nextLong(
                minimumSecondsInclusive,
                maximumSecondsExclusive
        );
        sleep(Duration.ofSeconds(seconds));
    }

    private ReactiveHelloWorld__PublisherTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
