package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Map<Class<?>, Logger> LOGS = new WeakHashMap<>();

    /**
     * Returns a logger for specified class.
     *
     * @param clazz the class.
     * @return a logger.
     */
    static Logger log(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        synchronized (LOGS) {
            return LOGS.computeIfAbsent(clazz, LoggerFactory::getLogger);
        }
    }

    /**
     * A map of classes and {@link System.Logger}s.
     */
    private static Map<Class<?>, System.Logger> LOGGERS = new WeakHashMap<>();

    /**
     * Returns a logger for specified class.
     *
     * @param clazz the class.
     * @return a logger.
     */
    static System.Logger logger(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        synchronized (LOGGERS) {
            return LOGGERS.computeIfAbsent(clazz, k -> System.getLogger(k.getName()));
        }
    }

    /**
     * A private constructor which always throws an {@link AssertionError}.
     */
    private HelloWorldLoggers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
