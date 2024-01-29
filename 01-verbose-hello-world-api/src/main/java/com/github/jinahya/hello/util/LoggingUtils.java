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
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
// https://gist.github.com/nkcoder/cd74919fd80594c56e09b448a2d1ba31
final class LoggingUtils {

    private static final Class<?> CLASS_LOGGER_CONTEXT;

    private static final Method METHOD_GET_LOGGER_LIST;

    static {
        try {
            CLASS_LOGGER_CONTEXT = Class.forName("ch.qos.logback.classic.LoggerContext");
            METHOD_GET_LOGGER_LIST = CLASS_LOGGER_CONTEXT.getMethod("getLoggerList");
        } catch (final ReflectiveOperationException roe) {
            throw new ExceptionInInitializerError(roe);
        }
    }

    static void setLevel(final String loggerName, final Level level) {
        Objects.requireNonNull(loggerName, "loggerName is null");
        try {
            LogbackUtils.setLevel(LoggerFactory.getLogger(loggerName), level);
        } catch (final ReflectiveOperationException roe) {
            log.error("failed to set level({}) on '{}'", level, loggerName);
        }
    }

    static void setLevel(final String loggerName, final String levelName) {
        Objects.requireNonNull(loggerName, "loggerName is null");
        setLevel(loggerName, Level.valueOf(levelName));
    }

    static void setLevelForAllLoggers(final Level level) {
        Objects.requireNonNull(level, "level is null");
        try {
            LogbackUtils.setLevel(LoggerFactory.getILoggerFactory(), level);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to set level(" + level + ") for all loggers", roe);
        }
    }

    static void setLevelForAllLoggers(final String levelName) {
        setLevelForAllLoggers(Level.valueOf(levelName));
    }

    private LoggingUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
