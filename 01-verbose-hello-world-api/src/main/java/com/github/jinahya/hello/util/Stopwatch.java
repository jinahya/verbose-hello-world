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
import java.util.function.LongFunction;

/**
 * .
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/time/StopWatch.html">StopWatch</a>
 * (org.apache.commons.lang3.time)
 * @see <a
 * href="https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/time/StopWatch.java">StopWatch.java</a>
 * (apache/commons-lang)
 * @see <a
 * href="https://javadoc.io/doc/com.google.guava/guava/latest/com/google/common/base/Stopwatch.html">Stopwatch</a>
 * (com.google.guava)
 * @see <a
 * href="https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Stopwatch.java">Stopwatch.java</a>
 * (google/guava)
 * @see <a
 * href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/StopWatch.html">StopWatch</a>
 * (org.springframework.util)
 * @see <a
 * href="https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/util/StopWatch.java">StopWatch.java</a>
 * (spring-projects/spring-framework)
 */
public final class Stopwatch {

    private static final StopwatchProvider<Void> INSTANCE = StopwatchProvider1.INSTANCE;

    /**
     * Starts a stopwatch bound to current thread.
     *
     * @return a carrier to be used with {@link #stopStopwatch(Object)}.
     */
    public static Object startStopwatch() {
        return INSTANCE.start(StopwatchProvider.DEFAULT_SUPPLIER);
    }

    public static <R> R stopStopwatch(final Object carrier,
                                      final LongFunction<? extends R> mapper) {
        return INSTANCE.stopHelper(carrier, StopwatchProvider.DEFAULT_SUPPLIER, mapper);
    }

    /**
     * Returns a duration elapsed since {@link #startStopwatch()} method invoked.
     *
     * @param carrier a carrier resulted from {@link #startStopwatch()}.
     */
    public static Duration stopStopwatch(final Object carrier) {
        return stopStopwatch(carrier, StopwatchProvider.DEFAULT_MAPPER);
    }

    /**
     * Creates a new instance.
     */
    private Stopwatch() {
        throw new AssertionError("instantiation is not allowed");
    }
}
