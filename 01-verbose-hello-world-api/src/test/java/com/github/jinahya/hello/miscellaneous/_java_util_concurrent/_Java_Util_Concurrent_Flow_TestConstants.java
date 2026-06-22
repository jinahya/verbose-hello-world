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
