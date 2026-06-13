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

import java.lang.invoke.*;
import java.util.*;

/**
 * A class providing test utilities for
 * {@link java.lang.invoke.MethodHandles java.lang.invoke.MethodHandles}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class _Java_Lang_Invoke_MethodHandles_TestUtils {

    /**
     * Returns a {@link MethodHandle} bound to the constructor of the specified class that is
     * invocable with the specified actual arguments. The underlying constructor is located by
     * {@link _Java_Lang_Reflect_TestUtils#findConstructor(Class, Object...)}, then
     * {@linkplain java.lang.reflect.AccessibleObject#setAccessible(boolean) suppressed-access} is
     * enabled so the returned handle works regardless of whether the specified
     * {@link MethodHandles.Lookup lookup} has direct access to the constructor's declaring class.
     *
     * @param lookup the lookup to use when
     *               {@linkplain MethodHandles.Lookup#unreflectConstructor unreflecting} the located
     *               constructor; must not be {@code null}.
     * @param clazz  the class whose constructors are searched; must not be {@code null}.
     * @param args   the actual arguments (may contain {@code null}); must not be {@code null}.
     * @param <T>    the type of {@code clazz}.
     * @return a {@link MethodHandle} for the matching constructor whose return type is
     * {@code clazz}; never {@code null}.
     * @throws NullPointerException     if {@code lookup}, {@code clazz}, or {@code args} is
     *                                  {@code null}.
     * @throws NoSuchElementException   if no declared constructor matches.
     * @throws IllegalArgumentException if more than one constructor matches and none is uniquely
     *                                  most specific.
     * @throws IllegalAccessException   if the located constructor cannot be unreflected through the
     *                                  given {@code lookup}.
     * @see _Java_Lang_Reflect_TestUtils#findConstructor(Class, Object...)
     */
    public static <T> MethodHandle findConstructor(
            final MethodHandles.Lookup lookup, final Class<T> clazz, final Object... args)
            throws IllegalAccessException {
        Objects.requireNonNull(lookup, "lookup is null");
        final var ctor = _Java_Lang_Reflect_TestUtils.findConstructor(clazz, args);
        ctor.setAccessible(true);
        return lookup.unreflectConstructor(ctor);
    }

    /**
     * The {@link List}-typed counterpart of
     * {@link #findConstructor(MethodHandles.Lookup, Class, Object...) findConstructor(lookup,
     * clazz, Object...)}; convenient for call sites that already hold the arguments in a list (e.g.
     * {@code MockedConstruction.Context#arguments()}).
     *
     * @param lookup the lookup to use when unreflecting the located constructor; must not be
     *               {@code null}.
     * @param clazz  the class whose constructors are searched; must not be {@code null}.
     * @param args   the actual arguments (may contain {@code null}); must not be {@code null}.
     * @param <T>    the type of {@code clazz}.
     * @return a {@link MethodHandle} for the matching constructor; never {@code null}.
     * @throws NullPointerException     if {@code lookup}, {@code clazz}, or {@code args} is
     *                                  {@code null}.
     * @throws NoSuchElementException   if no declared constructor matches.
     * @throws IllegalArgumentException if more than one constructor matches and none is uniquely
     *                                  most specific.
     * @throws IllegalAccessException   if the located constructor cannot be unreflected through the
     *                                  given {@code lookup}.
     * @see #findConstructor(MethodHandles.Lookup, Class, Object...)
     */
    public static <T> MethodHandle findConstructor(
            final MethodHandles.Lookup lookup, final Class<T> clazz, final List<?> args)
            throws IllegalAccessException {
        Objects.requireNonNull(args, "args is null");
        return findConstructor(lookup, clazz, args.toArray());
    }

    // ---------------------------------------------------------------------------------------------
    private _Java_Lang_Invoke_MethodHandles_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
