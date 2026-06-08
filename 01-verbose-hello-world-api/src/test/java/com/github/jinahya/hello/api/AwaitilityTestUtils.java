package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.util.*;
import lombok.extern.slf4j.*;
import org.awaitility.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.time.temporal.*;

/**
 * A class providing test utilities for {@link Awaitility} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class AwaitilityTestUtils {

    // ---------------------------------------------------------------------------------- Awaitility

    /**
     * Awaits for the specified duration.
     *
     * @param duration the duration to await.
     */
    public static void awaitFor(final Duration duration) {
        log.debug("awaiting for {}...", duration);
        Awaitility.await()
                .timeout(duration.plusSeconds(1L))
                .pollDelay(duration)
                .untilAsserted(() -> Assertions.assertTrue(true));
    }

    /**
     * Awaits for the specified amount of the specified temporal unit.
     *
     * @param amount the amount to await.
     * @param unit   the temporal unit of the given amount.
     */
    public static void awaitFor(final long amount, final TemporalUnit unit) {
        awaitFor(Duration.of(amount, unit));
    }

    /**
     * Awaits for one second.
     */
    public static void awaitForOneSecond() {
        awaitFor(1L, ChronoUnit.SECONDS);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private AwaitilityTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
