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

import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Slf4j
@SuppressWarnings({
        "java:S100" // ..._
})
// https://gist.github.com/nkcoder/cd74919fd80594c56e09b448a2d1ba31
final class LogbackUtils {

    private static final Class<?> LOGGER_CONTEXT_CLASS;

    private static final Method LOGGER_CONTEXT__GET_LOGGER_LIST_;

    static final String LEVEL_CLASS_NAME = "ch.qos.logback.classic.Level";

    static final Class<?> LEVEL_CLASS;

    private static final Method LEVEL_CLASS__TO_LEVEL_;

    static final String LOGGER_CLASS_NAME = "ch.qos.logback.classic.Logger";

    static final Class<?> LOGGER_CLASS;

    private static final Method LOGGER__GET_LEVEL_;

    private static final Method LOGGER__SET_LEVEL_;

    static {
        try {
            LOGGER_CONTEXT_CLASS = Class.forName("ch.qos.logback.classic.LoggerContext");
            LOGGER_CONTEXT__GET_LOGGER_LIST_ = LOGGER_CONTEXT_CLASS.getMethod("getLoggerList");
            LEVEL_CLASS = Class.forName(LEVEL_CLASS_NAME);
            LEVEL_CLASS__TO_LEVEL_ = LEVEL_CLASS.getDeclaredMethod("toLevel", String.class);
            LOGGER_CLASS = Class.forName(LOGGER_CLASS_NAME);
            LOGGER__GET_LEVEL_ = LOGGER_CLASS.getMethod("getLevel");
            LOGGER__SET_LEVEL_ = LOGGER_CLASS.getMethod("setLevel", LEVEL_CLASS);
        } catch (final ReflectiveOperationException roe) {
            throw new ExceptionInInitializerError(roe);
        }
    }

    private static Object toLogbackLevel_(final String name) throws ReflectiveOperationException {
        Objects.requireNonNull(name, "name is null");
        return LEVEL_CLASS__TO_LEVEL_.invoke(null, name);
    }

    static Object toLogbackLevel(final Level level) throws ReflectiveOperationException {
        Objects.requireNonNull(level, "level is null");
        return toLogbackLevel_(level.name());
    }

    static Object getLevel_(final Object logger) throws ReflectiveOperationException {
        return LOGGER__GET_LEVEL_.invoke(logger);
    }

    private static void setLevel_(final Object logger, final Object level)
            throws ReflectiveOperationException {
        LOGGER__SET_LEVEL_.invoke(logger, level);
    }

    static void setLevel(final Logger logger, final Level level)
            throws ReflectiveOperationException {
        setLevel_(logger, toLogbackLevel(level));
    }

    private static void setLevel_(final ILoggerFactory factory, final Object level)
            throws ReflectiveOperationException {
        Objects.requireNonNull(factory, "factory is null");
        final var list = (List<?>) LOGGER_CONTEXT__GET_LOGGER_LIST_.invoke(factory);
        for (final Object logbackLogger : list) {
            setLevel_(logbackLogger, level);
        }
    }

    static void setLevel(final ILoggerFactory factory, final Level level)
            throws ReflectiveOperationException {
        setLevel_(factory, toLogbackLevel(level));
    }

    private LogbackUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
