package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import java.time.Duration;

/**
 * System utilities.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class HelloWorldSystemUtils {

    private static final ThreadLocal<Long> START_NANOS1 = ThreadLocal.withInitial(System::nanoTime);

    public static void startStopWatch1() {
        START_NANOS1.set(System.nanoTime());
    }

    public static Duration stopStopWatch1() {
        try {
            return Duration.ofNanos(System.nanoTime() - START_NANOS1.get());
        } finally {
            START_NANOS1.remove();
        }
    }

//    private static final ScopedValue<Long> START_NANOS2 = ScopedValue.newInstance();
//
//    public static ScopedValue.Carrier<Long> startStopWatch2() {
//        return ScopedValue.where(START_NANOS2, System.nanoTime());
//    }
//
//    public static Duration stopStopWatch2(ScopedValue.Carrier<Long> carrier) {
//        Objects.requireNonNull(carrier, "carrier is null");
//        return carrier.call(v -> Duration.ofNanos(System.nanoTime() - v));
//    }

    private HelloWorldSystemUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
