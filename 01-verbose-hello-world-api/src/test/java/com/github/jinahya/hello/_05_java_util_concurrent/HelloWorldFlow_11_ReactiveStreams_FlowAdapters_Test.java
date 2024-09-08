package com.github.jinahya.hello._05_java_util_concurrent;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tests {@link FlowAdapters} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldFlow_11_ReactiveStreams_FlowAdapters_Test extends _HelloWorldFlowTest {

    /**
     * A simple subscriber which requests {@value HelloWorld#BYTES} or fewer items on its
     * {@link Subscriber#onSubscribe(Subscription) onSubscribe(subscription)} method, and
     * {@link Subscription#cancel() cancels} the subscription on the last item.
     *
     * @param <T> item type parameter
     */
    private static final class ReactiveStreamsSubscriber<T> // @formatter:off
            implements org.reactivestreams.Subscriber<T> {
        private ReactiveStreamsSubscriber(final int n) {
            super();
            if (n <= 0) {
                throw new IllegalArgumentException("n(" + n + ") < 0");
            }
            this.n = n;
        }
        @Override public String toString() {
            return String.format("[reactive-streams-subscriber@%1$08x]", hashCode());
        }
        @Override public void onSubscribe(final Subscription s) {
            log.debug("{}.onSubscribe({})", this, s);
            this.s = s;
            i = 0;
            log.debug("  - requesting {} items...", n);
            this.s.request(n);
        }
        @Override public void onNext(final T t) {
            log.debug("{}.onNext({})", this, t);
            if (++i == n) {
                log.debug("  - cancelling the subscription...");
                this.s.cancel();
            }
        }
        @Override public void onError(final Throwable t) {
            log.error("{}.onError({})", this, t, t);
        }
        @Override public void onComplete() {
            log.debug("{}.onComplete()", this);
        }
        private final int n;
        private Subscription s;
        private int i;
    } // @formatter:on

    // ---------------------------------------------------------------------------------------------
    @DisplayName("toPublisher(flowPublisher)")
    @Nested
    class ToPublisherTest {

        @Test
        @SuppressWarnings({"unchecked"})
        void __byte() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(final org.reactivestreams.Publisher<? extends Byte> publisher) {
                    Objects.requireNonNull(publisher, "publisher is null");
                    final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES << 1) + 1;
                    publisher.subscribe(new ReactiveStreamsSubscriber<>(n));
                }
            } // @formatter:on
            final var service = service();
            final Flow.Publisher<Byte> publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR)
            );
            // intercept, publisher.subscribe(subscriber), to wrap the subscriber as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // intercept, subscriber.onSubscribe(subscription),
                //       to wrap the subscription as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(FlowAdapters.toPublisher(publisher));
            // -------------------------------------------------------------------------------- then
            // verify, publisher.subscribe(subscriber) invoked, once
            final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
            final var subscriber = subscriberCaptor.getValue();
            // verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().atMost(Duration.ofSeconds(16L)).untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.atMost(n)).onNext(ArgumentMatchers.notNull());
            });
            if (n < HelloWorld.BYTES) {
                // verify, subscription.cancel() invoked, once
                Awaitility.await().atMost(Duration.ofSeconds(8L)).untilAsserted(() -> {
                    Mockito.verify(subscription, Mockito.times(1)).cancel();
                });
            } else {
                // verify, subscriber.onComplete() invoked, once
                Awaitility.await().atMost(Duration.ofSeconds(8L)).untilAsserted(() -> {
                    Mockito.verify(subscriber, Mockito.times(1)).onComplete();
                });
            }
        }

        @Test
        @SuppressWarnings({"unchecked"})
        void __array() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(
                        final org.reactivestreams.Publisher<? extends byte[]> publisher) {
                    Objects.requireNonNull(publisher, "publisher is null");
                    final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES >> 1) + 1;
                    publisher.subscribe(new ReactiveStreamsSubscriber<>(n));
                }
            } // @formatter:on
            final var service = service();
            final Flow.Publisher<byte[]> publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfArray(service, EXECUTOR)
            );
            // intercept, publisher.subscribe(subscriber) to wrap the subscriber as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(FlowAdapters.toPublisher(publisher));
            // -------------------------------------------------------------------------------- then
            // verify, publisher.subscribe(subscriber) invoked, once
            final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
            final var subscriber = subscriberCaptor.getValue();
            // verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().atMost(Duration.ofSeconds(8L)).untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.atMost(n)).onNext(ArgumentMatchers.notNull());
            });
            // verify, subscription.cancel() invoked, once
            Awaitility.await().atMost(Duration.ofSeconds(4L)).untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.times(1)).cancel();
            });
        }

        @Test
        void __buffer() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(
                        final org.reactivestreams.Publisher<? extends ByteBuffer> publisher) {
                    Objects.requireNonNull(publisher, "publisher is null");
                    final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES >> 1) + 1;
                    publisher.subscribe(new ReactiveStreamsSubscriber<>(n));
                }
            } // @formatter:on
            final var service = service();
            final Flow.Publisher<ByteBuffer> publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR)
            );
            // intercept, publisher.subscribe(subscriber) to wrap the subscriber as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(FlowAdapters.toPublisher(publisher));
            // -------------------------------------------------------------------------------- then
            // verify, publisher.subscribe(subscriber) invoked, once
            final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
            final var subscriber = subscriberCaptor.getValue();
            // verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.atMost(n)).onNext(ArgumentMatchers.notNull());
            });
            // verify, subscription.cancel() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.times(1)).cancel();
            });
        }

        @Test
        @SuppressWarnings({
                "java:S2699" // TODO: remove when implemented
        })
        void __string() {
            // TODO: Test!
        }
    }

    @DisplayName("toFlowPublisher(reactiveStreamsSubscriber)")
    @Nested
    class ToFlowSubscriberTest {

        @Test
        void __byte() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default org.reactivestreams.Subscriber<Byte> doWith() {
                    final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES << 1) + 1;
                    return new ReactiveStreamsSubscriber<>(n);
                }
            } // @formatter:on
            final var service = service();
            final Flow.Publisher<Byte> publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR)
            );
            final org.reactivestreams.Subscriber<Byte> subscriber = Mockito.spy(
                    new AlienService() {
                    }.doWith()
            );
            // intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
            BDDMockito.willAnswer(i -> {
                i.getArguments()[0] = Mockito.spy(i.getArgument(0, Subscription.class));
                return i.callRealMethod();
            }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(FlowAdapters.toFlowSubscriber(subscriber));
            // -------------------------------------------------------------------------------- then
            // verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.atMost(n)).onNext(ArgumentMatchers.notNull());
            });
            if (n < HelloWorld.BYTES) {
                // verify, subscription.cancel() invoked, once
                Awaitility.await().untilAsserted(() -> {
                    Mockito.verify(subscription, Mockito.times(1)).cancel();
                });
            } else {
                // verify, subscriber.onComplete() invoked, once
                Awaitility.await().untilAsserted(() -> {
                    Mockito.verify(subscriber, Mockito.times(1)).onComplete();
                });
            }
        }

        @Test
        void __array() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default org.reactivestreams.Subscriber<byte[]> doWith() {
                    final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES >> 1) + 1;
                    return new ReactiveStreamsSubscriber<>(n);
                }
            } // @formatter:on
            final var service = service();
            final Flow.Publisher<byte[]> publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfArray(service, EXECUTOR)
            );
            final org.reactivestreams.Subscriber<byte[]> subscriber = Mockito.spy(
                    new AlienService() {
                    }.doWith()
            );
            // intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
            BDDMockito.willAnswer(i -> {
                i.getArguments()[0] = Mockito.spy(i.getArgument(0, Subscription.class));
                return i.callRealMethod();
            }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(FlowAdapters.toFlowSubscriber(subscriber));
            // -------------------------------------------------------------------------------- then
            // verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // DONE, verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
            });
            // verify, subscription.cancel() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.times(1)).cancel();
            });
        }

        @Test
        void __buffer() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default org.reactivestreams.Subscriber<ByteBuffer> doWith() {
                    final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES >> 1) + 1;
                    return new ReactiveStreamsSubscriber<>(n);
                }
            } // @formatter:on
            final var service = service();
            final Flow.Publisher<ByteBuffer> publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR)
            );
            final org.reactivestreams.Subscriber<ByteBuffer> subscriber = Mockito.spy(
                    new AlienService() {
                    }.doWith()
            );
            // intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
            BDDMockito.willAnswer(i -> {
                i.getArguments()[0] = Mockito.spy(i.getArgument(0, Subscription.class));
                return i.callRealMethod();
            }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(FlowAdapters.toFlowSubscriber(subscriber));
            // -------------------------------------------------------------------------------- then
            // verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
            });
            // verify, subscription.cancel() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.times(1)).cancel();
            });
        }

        @Test
        @SuppressWarnings({
                "java:S2699" // TODO: remove when implemented
        })
        void __string() {
            // TODO: Test!
        }
    }
}
