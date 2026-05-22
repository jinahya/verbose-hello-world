/**
 * The Verbose Hello World Lib package. Provides a concrete implementation of
 * {@link com.github.jinahya.hello.api.HelloWorld HelloWorld} —
 * {@link com.github.jinahya.hello.lib.HelloWorldImpl HelloWorldImpl} — whose
 * {@link com.github.jinahya.hello.lib.HelloWorldImpl#set(byte[], int) set(array, index)} writes the
 * {@value com.github.jinahya.hello.api.HelloWorld#BYTES} {@code US-ASCII} bytes of
 * {@code "hello, world"} into {@code array} starting at {@code index} via direct byte-by-byte
 * assignment. The class is registered for {@link java.util.ServiceLoader ServiceLoader} discovery,
 * so consumers depending on this module at runtime can resolve it via
 * {@code ServiceLoader.load(HelloWorld.class).iterator().next()}.
 * <p>
 * The whole package is {@linkplain org.jspecify.annotations.NullMarked null-marked} — references
 * default to non-null unless explicitly annotated
 * {@link org.jspecify.annotations.Nullable &#64; /*- #%L verbose-hello-world-lib %% Copyright (C)
 * 2018 - 2026 Jinahya, Inc. %% Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. #L%
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.hello.lib;
