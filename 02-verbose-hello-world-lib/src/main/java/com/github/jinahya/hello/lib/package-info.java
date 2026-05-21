/**
 * The Verbose Hello World Lib package. Provides a concrete implementation of
 * {@link com.github.jinahya.hello.api.HelloWorld HelloWorld} —
 * {@link com.github.jinahya.hello.lib.HelloWorldImpl HelloWorldImpl} — whose
 * {@link com.github.jinahya.hello.lib.HelloWorldImpl#set(byte[], int) set(array, index)} writes
 * the {@value com.github.jinahya.hello.api.HelloWorld#BYTES} {@code US-ASCII} bytes of
 * {@code "hello, world"} into {@code array} starting at {@code index} via direct byte-by-byte
 * assignment. The class is registered for
 * {@link java.util.ServiceLoader ServiceLoader} discovery, so consumers depending on this module
 * at runtime can resolve it via {@code ServiceLoader.load(HelloWorld.class).iterator().next()}.
 * <p>
 * The whole package is {@linkplain org.jspecify.annotations.NullMarked null-marked} — references
 * default to non-null unless explicitly annotated {@link org.jspecify.annotations.Nullable
 * &#64;Nullable}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.hello.lib;
