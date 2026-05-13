package com.github.jinahya.hello.api;

import org.reactivestreams.Publisher;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A utility class for creating {@link Publisher Reactive Streams Publishers} that emit the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> in one of three element shapes.
 * <p>
 * Each factory method returns a fresh, single-use {@link Publisher} backed by the supplied
 * {@link HelloWorld} service. The three shapes are layered, each composed on top of the previous:
 * <ul>
 *   <li>{@link #ofBytes(HelloWorld)} — reads the payload from the {@code service} directly and
 *       publishes one {@link Byte} per byte.</li>
 *   <li>{@link #ofArrays(HelloWorld)} — composes on top of {@link #ofBytes(HelloWorld)} to
 *       assemble {@value HelloWorld#BYTES}-byte arrays, one {@code byte[]} per downstream
 *       demand.</li>
 *   <li>{@link #ofStrings(HelloWorld)} — composes on top of {@link #ofArrays(HelloWorld)} to
 *       decode each array as a {@link StandardCharsets#US_ASCII US-ASCII} string, one
 *       {@link String} per upstream array.</li>
 * </ul>
 * <p>
 * For the JDK {@link java.util.concurrent.Flow} equivalent (same publishers, wrapped as
 * {@link java.util.concurrent.Flow.Publisher}), see {@link HelloWorldFlow}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld
 * @see Publisher
 * @see HelloWorldFlow
 */
public final class ReactiveHelloWorldPublishers {

    /**
     * Returns a new {@link Publisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of individual
     * {@link Byte} elements, one per byte of the payload, in order.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link Publisher} of {@link Byte}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public static Publisher<Byte> ofBytes(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return new ReactiveHelloWorldBytePublisher(service);
    }

    /**
     * Returns a new {@link Publisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of
     * {@code byte[]} elements, each a fresh {@value HelloWorld#BYTES}-byte snapshot of the
     * payload.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link Publisher} of {@code byte[]}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public static Publisher<byte[]> ofArrays(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return new ReactiveHelloWorldArrayPublisher(ofBytes(service));
    }

    /**
     * Returns a new {@link Publisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of
     * {@link String} elements, each decoded in {@link StandardCharsets#US_ASCII US-ASCII} from one
     * upstream {@code byte[]}.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link Publisher} of {@link String}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public static Publisher<String> ofStrings(final HelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return new ReactiveHelloWorldStringPublisher(ofArrays(service));
    }

    private ReactiveHelloWorldPublishers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
