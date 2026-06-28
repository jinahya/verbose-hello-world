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

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Package-private utilities shared by the Reactive Streams publisher implementations
 * ({@link ReactiveHelloWorldBytePublisher}, and {@link ReactiveHelloWorldArrayPublisher}.
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
    static long aggregateDemand(final AtomicLong demand, final long n) {
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
