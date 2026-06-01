/**
 * The Verbose Hello World API package. Centers on the
 * {@link com.github.jinahya.hello.api.HelloWorld HelloWorld}
 * {@link java.lang.FunctionalInterface functional interface} — whose single abstract method
 * {@link com.github.jinahya.hello.api.HelloWorld#set(byte[], int) set(array, index)} writes the
 * {@value com.github.jinahya.hello.api.HelloWorld#BYTES} {@code US-ASCII} bytes of
 * {@code "hello, world"} — and surrounds it with {@code default} methods that exercise every major
 * Java I/O pathway in the JDK ({@link java.io}, {@link java.nio}, {@link java.net},
 * {@link java.nio.channels asynchronous channels}, {@link java.security} / {@link javax.crypto},
 * {@link java.net.http HTTP}, {@link java.util.concurrent.Flow Reactive Streams}, &hellip;). The
 * asynchronous counterpart
 * {@link com.github.jinahya.hello.api.AsynchronousHelloWorld AsynchronousHelloWorld} and the
 * package-private {@code ReactiveHelloWorld*Publisher/Processor} implementations build on top.
 * <p>
 * The whole package is {@linkplain org.jspecify.annotations.NullMarked null-marked} — references
 * default to non-null unless explicitly annotated
 * {@link org.jspecify.annotations.Nullable &#64;Nullable}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.hello.api;

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
