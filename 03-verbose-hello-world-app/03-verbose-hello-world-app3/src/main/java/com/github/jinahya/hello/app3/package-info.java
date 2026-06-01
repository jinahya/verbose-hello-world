/**
 * The Verbose Hello World App3 package. Hosts
 * {@link com.github.jinahya.hello.app3.HelloWorldMain HelloWorldMain} and
 * {@link com.github.jinahya.hello.app3.HelloWorldConfiguration HelloWorldConfiguration}, which
 * together wire an
 * {@link com.github.jinahya.hello.api.AsynchronousHelloWorld AsynchronousHelloWorld} via
 * <a href="https://spring.io/projects/spring-framework">Spring</a> dependency injection,
 * asynchronously append {@code hello, world} to a temporary file, and print it back to
 * {@link java.lang.System#out System.out}.
 * <p>
 * The whole package is {@linkplain org.jspecify.annotations.NullMarked null-marked} — references
 * default to non-null unless explicitly annotated
 * {@link org.jspecify.annotations.Nullable &#64;Nullable}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.hello.app3;

/*-
 * #%L
 * verbose-hello-world-app3
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
