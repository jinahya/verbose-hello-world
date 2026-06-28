package com.github.jinahya.hello.miscellaneous._java_util_concurrent;

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

import lombok.extern.slf4j.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

@Slf4j
public final class _Java_Util_Concurrent_Flow_TestUtils {

    // ---------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T> Flow.Subscriber<T> logging(
            final Flow.Subscriber<T> subscriber,
            final Function<? super Object[], ? extends String> formatter) {
        return (Flow.Subscriber<T>) Proxy.newProxyInstance(
                subscriber.getClass().getClassLoader(),
                new Class[] {Flow.Subscriber.class},
                (p, m, v) -> {
                    if (m.getDeclaringClass() == Flow.Subscriber.class) {
                        log.debug("{}({})", m.getName(), formatter.apply(v));
                    }
                    return m.invoke(subscriber, v);
                }
        );
    }

    public static <T> Flow.Subscriber<T> loggingByte(final Flow.Subscriber<T> subscriber) {
        return logging(subscriber, v -> String.format("0x%02x", v));
    }

    // ---------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    public static <T> Flow.Subscriber<T> logging(final Flow.Subscriber<T> subscriber) {
        return (Flow.Subscriber<T>) Proxy.newProxyInstance(
                subscriber.getClass().getClassLoader(),
                new Class[] {Flow.Subscriber.class},
                (p, m, v) -> {
                    if (m.getDeclaringClass() == Flow.Subscriber.class) {
                        log.debug("{}({})", m.getName(), v.length == 1 ? v : Arrays.toString(v));
                    }
                    return m.invoke(subscriber, v);
                }
        );
    }

    public static <T> Flow.Subscription logging(final Flow.Subscription subscription) {
        Proxy.newProxyInstance(
                subscription.getClass().getClassLoader(),
                new Class[] {Flow.Subscriber.class},
                (p, m, v) -> {
                    if (m.getDeclaringClass() == Flow.Subscriber.class) {
                        log.debug("{}({})", m.getName(), Arrays.toString(v));
                    }
                    return m.invoke(subscription, v);
                }
        );
        return null;
    }

    // ---------------------------------------------------------------------------------------------
    private _Java_Util_Concurrent_Flow_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
