package com.github.jinahya.hello;

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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * A helper class for managing loggers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldLoggers {

    /**
     * A map of classes and {@link Logger}s.
     */
    private static final Map<Class<?>, Logger> LOGS = Collections.synchronizedMap(
            new WeakHashMap<>());

    /**
     * Returns a logger for specified class.
     *
     * @param clazz the class.
     * @return a logger.
     */
    static Logger log(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        return LOGS.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }

    /**
     * A map of classes and {@link System.Logger}s.
     */
    private static final Map<Class<?>, System.Logger> LOGGERS
            = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Returns a logger for specified class.
     *
     * @param clazz the class.
     * @return a logger.
     */
    static System.Logger logger(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        return LOGGERS.computeIfAbsent(clazz, k -> System.getLogger(k.getName()));
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldLoggers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
