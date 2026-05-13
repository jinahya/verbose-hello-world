package com.github.jinahya.hello.api;

/**
 * A sealed {@link org.reactivestreams.Publisher} that publishes the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> in one of three element shapes:
 * <ul>
 *   <li>individual {@link Byte} elements — one per byte of the payload, in order.</li>
 *   <li>{@code byte[]} chunks of the payload.</li>
 *   <li>the payload as a {@link String}.</li>
 * </ul>
 * Each shape is realised by a non-public, sealed implementation reachable only through the static
 * factory methods on this interface.
 * <p>
 * The interface itself adds no methods beyond
 * {@link org.reactivestreams.Publisher#subscribe(org.reactivestreams.Subscriber) subscribe}; its
 * sole purpose is to seal the family of hello-world-shaped publishers and to expose static
 * factory methods specialised for each element type.
 * <p>
 * Implementations follow the <a href="https://www.reactive-streams.org/">Reactive Streams</a>
 * specification. In particular, subscribers must observe the serial-call contract of
 * <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#2.7">Rule
 * 2.7</a> when invoking {@code Subscription.request} and {@code Subscription.cancel}, and
 * publishers honour the thread-safety requirement on {@code cancel}
 * (<a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.5">Rule
 * 3.5</a>).
 *
 * @param <T> the element type produced by this publisher; {@link Byte}, {@code byte[]}, or
 *            {@link String}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld
 * @see org.reactivestreams.Publisher
 */
public sealed interface ReactiveHelloWorldPublisher<T>
        extends org.reactivestreams.Publisher<T>
        permits ReactiveHelloWorldBytePublisher,
                ReactiveHelloWorldArrayPublisher,
                ReactiveHelloWorldStringPublisher {

    /**
     * Returns a new {@link ReactiveHelloWorldPublisher} that publishes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as a sequence of individual
     * {@link Byte} elements, one per byte of the payload, in order.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @return a new {@link ReactiveHelloWorldPublisher} of {@link Byte}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    static ReactiveHelloWorldPublisher<Byte> newInstanceForBytes(final HelloWorld service) {
        return new ReactiveHelloWorldBytePublisher(service);
    }
//
//    sealed interface OfArrays
//            extends HelloWorldPublisher<byte[]>
//            permits ReactiveHelloWorld_OfArrays {
//
//        static OfArrays newInstance(final HelloWorld service) {
//            return new ReactiveHelloWorld_OfArrays(OfBytes.newInstance(service));
//        }
//    }
//
//    sealed interface OfStrings
//            extends HelloWorldPublisher<String>
//            permits ReactiveHelloWorld_OfStrings {
//
//        static OfStrings newInstance(final HelloWorld service) {
//            return new ReactiveHelloWorld_OfStrings(OfArrays.newInstance(service));
//        }
//    }
}
