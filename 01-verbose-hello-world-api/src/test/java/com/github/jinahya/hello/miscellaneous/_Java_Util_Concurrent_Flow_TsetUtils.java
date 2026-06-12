package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

@Slf4j
public final class _Java_Util_Concurrent_Flow_TsetUtils {

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

    private _Java_Util_Concurrent_Flow_TsetUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
