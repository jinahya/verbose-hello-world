package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
// https://gist.github.com/nkcoder/cd74919fd80594c56e09b448a2d1ba31
public final class LoggingUtils {

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

    public static void setLevel(final String loggerName, final Level level) {
        Objects.requireNonNull(loggerName, "loggerName is null");
        try {
            LogbackUtils.setLevel(LoggerFactory.getLogger(loggerName), level);
        } catch (final ReflectiveOperationException roe) {
            log.error("failed to set level({}) on '{}'", level, loggerName);
        }
    }

    public static void setLevel(final String loggerName, final String levelName) {
        Objects.requireNonNull(loggerName, "loggerName is null");
        setLevel(loggerName, Level.valueOf(levelName));
    }

    public static void setLevelForAllLoggers(final Level level) {
        Objects.requireNonNull(level, "level is null");
        try {
            LogbackUtils.setLevel(LoggerFactory.getILoggerFactory(), level);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to set level(" + level + ") for all loggers", roe);
        }
    }

    public static void setLevelForAllLoggers(final String levelName) {
        setLevelForAllLoggers(Level.valueOf(levelName));
    }

    private LoggingUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
