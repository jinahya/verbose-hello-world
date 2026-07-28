package com.github.jinahya.hello.util;

import lombok.extern.slf4j.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.function.*;

@Slf4j
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class SocketOptionsUtils {

    public static List<? extends SocketOption<?>> standardSocketOptions() {
        return Arrays.stream(StandardSocketOptions.class.getFields())
                .filter(f -> {
                    // public static final
                    final var modifiers = f.getModifiers();
                    return Modifier.isPublic(modifiers) &&
                           Modifier.isStatic(modifiers) &&
                           Modifier.isFinal(modifiers);
                })
                .filter(f -> {
                    // SocketOption<?>
                    return SocketOption.class.isAssignableFrom(f.getType());
                })
                .map(f -> {
                    try {
                        return (SocketOption<?>) f.get(null);
                    } catch (final IllegalAccessException iae) {
                        log.error("failed to get the value of {}", f, iae);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static <T> void acceptSocketOptions(final T object,
                                               final BiFunction<? super T, ? super SocketOption<?>, ?> accessor,
                                               final BiConsumer<? super SocketOption<?>, Object> consumer) {
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(accessor, "accessor is null");
        Objects.requireNonNull(consumer, "consumer is null");
        standardSocketOptions().forEach(so -> {
            final var value = accessor.apply(object, so);
            consumer.accept(so, value);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static void acceptSocketOptions(final Socket socket,
                                           final BiConsumer<? super SocketOption<?>, Object> consumer) {
        SocketOptionsUtils.acceptSocketOptions(
                socket,
                (s, so) -> {
                    try {
                        return s.getOption(so);
                    } catch (final UnsupportedOperationException uoe) {
                        log.warn("failed to get {} from {}", so, s);
                        return null;
                    } catch (final IOException ioe) {
                        log.error("failed to get {} from {}", so, s, ioe);
                        return null;
                    }
                },
                consumer
        );
    }

    public static void logSocketOptions(final Socket socket) {
        acceptSocketOptions(socket, (so, v) -> {
            log.debug("{}: {}", so, v);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static void acceptSocketOptions(final ServerSocket socket,
                                           final BiConsumer<? super SocketOption<?>, Object> consumer) {
        SocketOptionsUtils.acceptSocketOptions(
                socket,
                (s, so) -> {
                    try {
                        return s.getOption(so);
                    } catch (final UnsupportedOperationException uoe) {
                        log.warn("failed to get {} from {}", so, s);
                        return null;
                    } catch (final IOException ioe) {
                        log.error("failed to get {} from {}", so, s, ioe);
                        return null;
                    }
                },
                consumer
        );
    }

    public static void logSocketOptions(final ServerSocket socket) {
        acceptSocketOptions(socket, (so, v) -> {
            log.debug("{}: {}", so, v);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    private SocketOptionsUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
