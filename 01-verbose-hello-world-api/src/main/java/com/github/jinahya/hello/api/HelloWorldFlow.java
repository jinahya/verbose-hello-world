package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

/**
 * A utility class for creating JDK {@link Flow.Publisher Flow.Publishers} that emit the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> in one of three element shapes.
 * <p>
 * Each factory delegates to the corresponding method on {@link ReactiveHelloWorldPublishers} to
 * produce an {@code org.reactivestreams.Publisher}, then wraps it as a {@link Flow.Publisher} via
 * {@link FlowAdapters#toFlowPublisher(org.reactivestreams.Publisher)} from the
 * <a href="https://search.maven.org/artifact/org.reactivestreams/reactive-streams-flow-adapters">
 * reactive-streams-flow-adapters</a> library.
 * <p>
 * The Reactive Streams interfaces and the JDK {@link Flow} nested interfaces have identical shape
 * (JEP 266 specifically aligned them), so the adapter is a thin wrapper that forwards every signal
 * 1:1. No publisher logic is duplicated; this class is purely a type bridge.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldPublishers
 * @see Flow
 * @see FlowAdapters
 */
public final class HelloWorldFlow {

    /**
     * Returns a new {@link Flow.Publisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of individual
     * {@link Byte} elements, one per byte of the payload, in order.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link Flow.Publisher} of {@link Byte}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public static Flow.Publisher<Byte> ofBytes(final HelloWorld service) {
        return FlowAdapters.toFlowPublisher(ReactiveHelloWorldPublishers.ofBytes(service));
    }

    /**
     * Returns a new {@link Flow.Publisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of
     * {@code byte[]} elements, each a fresh {@value HelloWorld#BYTES}-byte snapshot of the
     * payload.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link Flow.Publisher} of {@code byte[]}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public static Flow.Publisher<byte[]> ofArrays(final HelloWorld service) {
        return FlowAdapters.toFlowPublisher(ReactiveHelloWorldPublishers.ofArrays(service));
    }

    /**
     * Returns a new {@link Flow.Publisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of
     * {@link String} elements, each decoded in
     * {@link java.nio.charset.StandardCharsets#US_ASCII US-ASCII} from one upstream
     * {@code byte[]}.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link Flow.Publisher} of {@link String}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public static Flow.Publisher<String> ofStrings(final HelloWorld service) {
        return FlowAdapters.toFlowPublisher(ReactiveHelloWorldPublishers.ofStrings(service));
    }

    private HelloWorldFlow() {
        throw new AssertionError("instantiation is not allowed");
    }
}
