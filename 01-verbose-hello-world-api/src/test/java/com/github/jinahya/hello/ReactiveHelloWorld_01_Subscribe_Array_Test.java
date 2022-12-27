package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * A class for unit-testing {@link ReactiveHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(array)")
@Slf4j
class ReactiveHelloWorld_01_Subscribe_Array_Test extends ReactiveHelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[]) set(array)} method to just return the {@code array}
     * argument.
     */
    @org.junit.jupiter.api.BeforeEach
    void _ReturnArray_SetArray() {
        doAnswer(i -> i.getArgument(0)) // <1>
                .when(service())        // <2>
                .set(any());            // <3>
    }

    @DisplayName("(, s -> s.set(array)")
    @Test
    void __() throws InterruptedException {
        // GIVEN
        var service = service();
        var counter = new LongAdder();
//        var n = ThreadLocalRandom.current().nextLong(1024L) | 1L;
        var n = Long.MAX_VALUE;
        var latch = new CountDownLatch(1);
        var subscriber = new ReactiveHelloWorldSubscriber<byte[]>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                super.onSubscribe(subscription);
                subscription.request(n);
                new Thread(() -> {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException ie) {
                        currentThread().interrupt();
                    }
                    subscription.cancel();
                }).start();
            }

            @Override
            public void onNext(byte[] item) {
                super.onNext(item);
                counter.increment();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                latch.countDown();
            }

            @Override
            public void onComplete() {
                super.onComplete();
                assertTrue(counter.longValue() <= n);
                latch.countDown();
            }
        };
        // WHEN
        service.subscribe(subscriber, s -> s.set(new byte[BYTES]));
        latch.await();
    }
}
