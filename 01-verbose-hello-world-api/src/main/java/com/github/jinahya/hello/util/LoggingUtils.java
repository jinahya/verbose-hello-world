package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Slf4j
public class LoggingUtils {

    public static void setLevel(final String name, final String level) {
        Objects.requireNonNull(name, "name is null");
        final Logger loggerInterface = LoggerFactory.getLogger(name);
        if (!loggerInterface.isTraceEnabled()) {
            try {
                final var levelClass = Class.forName("ch.qos.logback.classic.Level");
                final var toLevel = levelClass.getDeclaredMethod("toLevel", String.class);
                final Object levelValue = toLevel.invoke(null, level);
                final var setLevel = loggerInterface.getClass()
                        .getDeclaredMethod("setLevel", levelClass);
                setLevel.invoke(loggerInterface, levelValue);
            } catch (final Exception e) {
                log.error("failed to set '" + level + "' on " + name);
            }
        }
    }

    private LoggingUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
