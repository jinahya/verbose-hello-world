package com.github.jinahya.hello.miscellaneous;

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

import java.lang.reflect.*;
import java.util.*;

/**
 * A class providing test utilities for {@link java.lang.reflect java.lang.reflect}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class _Java_Lang_Reflect_TestUtils {

    /**
     * Finds the {@linkplain Class#getDeclaredConstructors() declared constructor} of the specified
     * class whose parameter list is invocable with the specified actual arguments, applying the
     * same {@code null}-as-any-reference and primitive/wrapper boxing rules the JVM uses at an
     * actual call site. When more than one constructor matches, the
     * <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-15.html#jls-15.12.2.5">JLS
     * most-specific</a> one is returned; if no unique most-specific constructor exists, the call is
     * treated as ambiguous and {@link IllegalArgumentException} is thrown.
     * <p>
     * Intended for tests that intercept construction via Mockito's {@code mockConstruction(...)}
     * and receive the actual arguments as a {@code List<?>} (via
     * {@code MockedConstruction.Context#arguments()}); the helper picks the constructor the
     * intercepted {@code new} call would have resolved to, so the test does not have to enumerate
     * each arity by hand.
     *
     * @param clazz the class whose constructors are searched; must not be {@code null}.
     * @param args  the actual arguments (may contain {@code null}); must not be {@code null}.
     * @param <T>   the type of {@code clazz}.
     * @return the unique most-specific matching constructor; never {@code null}.
     * @throws NullPointerException     if {@code clazz} or {@code args} is {@code null}.
     * @throws NoSuchElementException   if no declared constructor matches.
     * @throws IllegalArgumentException if more than one constructor matches and none is uniquely
     *                                  most specific.
     */
    public static <T> Constructor<T> findConstructor(final Class<T> clazz, final Object... args) {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(args, "args is null");
        @SuppressWarnings({"unchecked", "rawtypes"})
        final var constructors = (Constructor<T>[]) (Constructor[]) clazz.getDeclaredConstructors();
        final var matches = new ArrayList<Constructor<T>>(2);
        for (final var constructor : constructors) {
            if (constructor.getParameterCount() != args.length) {
                continue;
            }
            final var types = constructor.getParameterTypes();
            var ok = true;
            for (int i = 0; i < args.length; i++) {
                if (!isInvocable(types[i], args[i])) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                matches.add(constructor);
            }
        }
        if (matches.isEmpty()) {
            throw new NoSuchElementException(
                    "no constructor of " + clazz.getName() + " is invocable with "
                    + Arrays.toString(args));
        }
        if (matches.size() == 1) {
            return matches.get(0);
        }
        final var picked = mostSpecific(matches);
        if (picked == null) {
            throw new IllegalArgumentException(
                    "ambiguous constructors of " + clazz.getName() + " for "
                    + Arrays.toString(args) + ": " + matches);
        }
        return picked;
    }

    /**
     * The {@link List}-typed counterpart of
     * {@link #findConstructor(Class, Object...) findConstructor(clazz, Object...)}; convenient for
     * call sites that already hold the arguments in a list (e.g.
     * {@code MockedConstruction.Context#arguments()}).
     *
     * @param clazz the class whose constructors are searched; must not be {@code null}.
     * @param args  the actual arguments (may contain {@code null}); must not be {@code null}.
     * @param <T>   the type of {@code clazz}.
     * @return the unique most-specific matching constructor; never {@code null}.
     * @throws NullPointerException     if {@code clazz} or {@code args} is {@code null}.
     * @throws NoSuchElementException   if no declared constructor matches.
     * @throws IllegalArgumentException if more than one constructor matches and none is uniquely
     *                                  most specific.
     * @see #findConstructor(Class, Object...)
     */
    public static <T> Constructor<T> findConstructor(final Class<T> clazz, final List<?> args) {
        Objects.requireNonNull(args, "args is null");
        return findConstructor(clazz, args.toArray());
    }

    // ---------------------------------------------------------------------------------------------
    static boolean isInvocable(final Class<?> paramType, final Object arg) {
        if (arg == null) {
            return !paramType.isPrimitive();
        }
        if (paramType.isInstance(arg)) {
            return true;
        }
        return paramType.isPrimitive() && wrap(paramType) == arg.getClass();
    }

    static Class<?> wrap(final Class<?> primitive) {
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == double.class) return Double.class;
        if (primitive == float.class) return Float.class;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == byte.class) return Byte.class;
        if (primitive == short.class) return Short.class;
        if (primitive == char.class) return Character.class;
        if (primitive == void.class) return Void.class;
        return primitive;
    }

    private static <T> Constructor<T> mostSpecific(final List<Constructor<T>> matches) {
        outer:
        for (final var a : matches) {
            for (final var b : matches) {
                if (a == b) {
                    continue;
                }
                if (!moreSpecificOrEqual(a.getParameterTypes(), b.getParameterTypes())) {
                    continue outer;
                }
            }
            return a;
        }
        return null;
    }

    private static boolean moreSpecificOrEqual(final Class<?>[] a, final Class<?>[] b) {
        for (int i = 0; i < a.length; i++) {
            final var ap = a[i];
            final var bp = b[i];
            if (ap == bp) {
                continue;
            }
            if (!bp.isPrimitive() && bp.isAssignableFrom(ap)) {
                continue;
            }
            if (ap.isPrimitive() && !bp.isPrimitive() && wrap(ap) == bp) {
                continue;
            }
            return false;
        }
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    private _Java_Lang_Reflect_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
