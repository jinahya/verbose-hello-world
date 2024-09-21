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
import com.github.jinahya.hello.HelloWorldTestUtils;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.FlowAdapters;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.LongSupplier;

@Slf4j
class HelloWorldFlow_12_RxJava extends _HelloWorldFlowTest {

    private static class AlienFunction<T> implements Function<Observable<T>, Disposable> {

        private AlienFunction(final LongSupplier countSupplier,
                              final io.reactivex.rxjava3.functions.Consumer<? super T> itemConsumer) {
            super();
            this.countSupplier = Objects.requireNonNull(countSupplier, "countSupplier is null");
            this.itemConsumer = Objects.requireNonNull(itemConsumer, "itemConsumer is null");
        }

        @Override
        public Disposable apply(Observable<T> observable) {
            return Objects.requireNonNull(observable, "observable is null")
                    .take(countSupplier.getAsLong())
                    .doOnNext(itemConsumer)
                    .subscribe();
        }

        private final LongSupplier countSupplier;

        private final io.reactivex.rxjava3.functions.Consumer<? super T> itemConsumer;
    }

    @Test
    @SuppressWarnings({"unchecked"})
    void __byte() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Flow.Publisher<Byte> publisher = Mockito.spy(
                new HelloWorldFlow.HelloWorldPublisher.OfByte(
                        service,
                        Executors.newVirtualThreadPerTaskExecutor()
                )
        );
        // intercept, <publisher.subscribe(subscriber)> to wrap the <subscriber> as a spy
        BDDMockito.willAnswer(i -> {
            final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
            // intercept, <subscriber.onSubscribe(subscription)> to wrap the <subscription> as a spy
            BDDMockito.willAnswer(j -> {
                final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                j.getArguments()[0] = subscription;
                return j.callRealMethod();
            }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
            i.getArguments()[0] = subscriber;
            return i.callRealMethod();
        }).given(publisher).subscribe(ArgumentMatchers.notNull());
        // prepare an observable
        final var observable =
                ThreadLocalRandom.current().nextBoolean()
                ? Observable.fromPublisher(FlowAdapters.toPublisher(publisher))
                : Flowable.fromPublisher(FlowAdapters.toPublisher(publisher)).toObservable();
        // ------------------------------------------------------------------------------------ when
        final var disposable = new AlienFunction<Byte>(
                () -> ThreadLocalRandom.current().nextLong(HelloWorld.BYTES, HelloWorld.BYTES << 1),
                i -> {
                    log.debug("item: '{}'", (char) i.byteValue());
                }
        ).apply(observable);
        HelloWorldTestUtils.awaitForOneSecond();
        // ------------------------------------------------------------------------------------ then
        // verify, <publisher.subscribe(subscriber)> invoked, once
        final Flow.Subscriber<Byte> subscriber;
        {
            final var captor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            Mockito.verify(publisher, Mockito.times(1)).subscribe(captor.capture());
            subscriber = captor.getValue();
        }
        // verify <subscriber.onSubscribe(subscription)> invoked, once
        final Flow.Subscription subscription;
        {
            final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(captor.capture());
            subscription = captor.getValue();
        }
        // verify <subscription.request(Long.MAX_VALUE)> invoked, once
        Mockito.verify(subscription, Mockito.times(1)).request(Long.MAX_VALUE);
        // verify, <subscriber.onNext(item)> invoked, at most <12>
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
//        if (AlienService.countRef.get() >= HelloWorld.BYTES) {
//            // verify, subscriber.onComplete() invoked, once
//            Mockito.verify(subscriber, Mockito.times(1)).onComplete();
//        } else {
//            // verify, subscriber.onComplete() invoked, never
//            Mockito.verify(subscriber, Mockito.never()).onComplete();
//        }
        // verify, <subscription.cancel()> invoked, once
        Mockito.verify(subscription, Mockito.times(1)).cancel();
        disposable.dispose();
    }
}
