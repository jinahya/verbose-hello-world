package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.*;

import java.lang.reflect.*;
import java.util.concurrent.*;

@Slf4j
public final class _Java_Util_Concurrent_Flow_TestConstants {

    static final Method ON_SUBSCRIBE;

    static {
        try {
            ON_SUBSCRIBE = Flow.Subscriber.class.getMethod("onSubscribe", Flow.Subscription.class);
        } catch (final NoSuchMethodException nsme) {
            throw new ExceptionInInitializerError(nsme);
        }
    }

    static final Method ON_NEXT;

    static {
        try {
            ON_NEXT = Flow.Subscriber.class.getMethod("onNext", Object.class);
        } catch (final NoSuchMethodException nsme) {
            throw new ExceptionInInitializerError(nsme);
        }
    }

    static final Method ON_ERROR;

    static {
        try {
            ON_ERROR = Flow.Subscriber.class.getMethod("onError", Throwable.class);
        } catch (final NoSuchMethodException nsme) {
            throw new ExceptionInInitializerError(nsme);
        }
    }

    static final Method ON_COMPLETE;

    static {
        try {
            ON_COMPLETE = Flow.Subscriber.class.getMethod("onComplete");
        } catch (final NoSuchMethodException nsme) {
            throw new ExceptionInInitializerError(nsme);
        }
    }

    // ---------------------------------------------------------------------------------------------
    private _Java_Util_Concurrent_Flow_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
