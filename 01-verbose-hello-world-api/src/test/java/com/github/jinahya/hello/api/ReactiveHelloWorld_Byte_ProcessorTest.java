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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.reactivestreams.*;

import java.time.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link ReactiveHelloWorldByteProcessor} with multiple downstream subscribers
 * attached to a single processor, each requesting a different total ({@code < 12}, {@code == 12},
 * {@code > 12}) and joining the processor with a random sub-second throttle.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ReactiveHelloWorld_Byte_ProcessorTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(30L);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_Byte_ProcessorTest() {
        super();
        service = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        set_array_sets_hello_world_bytes(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void __immediateClose() {
        try (final var processor = new ReactiveHelloWorldByteProcessor(service)) {
        }
    }

    @Test
    void __() { // @formatter:on
        // ----------------------------------------------------------------------------------- given
        final var ds = new int[] {
                HelloWorld.BYTES - 1, HelloWorld.BYTES + 1
        };
        try (final var processor = new ReactiveHelloWorldByteProcessor(service)) {
            final var subscribers = new ArrayList<Subscriber<Byte>>();
            for (final int d : ds) {
                final var subscriber = MockitoTestUtils.loggingSpy(new Subscriber<Byte>() {
                    @Override
                    public void onSubscribe(final Subscription s) {
                        Thread.ofVirtual().start(() -> {
                            for (int i = 0; i < d; i++) {
                                s.request(1L);
                            }
                        });
                    }

                    @Override
                    public void onNext(final Byte t) { /* drop */ }

                    @Override
                    public void onError(final Throwable t) { /* ignore */ }

                    @Override
                    public void onComplete() { /* ignore */ }
                });
                subscribers.add(subscriber);
                processor.subscribe(subscriber);
            }
            // -------------------------------------------------------------------------------- then
            for (int i = 0; i < ds.length; i++) {
                final var sub = subscribers.get(i);
                if (ds[i] >= HelloWorld.BYTES) {
                    verify(sub, timeout(TIMEOUT.toMillis()).times(1)).onComplete();
                } else {
                    verify(sub, timeout(TIMEOUT.toMillis()).times(ds[i])).onNext(any());
                }
            }
            for (int i = 0; i < ds.length; i++) {
                final var subscriber = subscribers.get(i);
                verify(subscriber, times(1)).onSubscribe(notNull());
                verify(subscriber, never()).onError(any());
                if (ds[i] >= HelloWorld.BYTES) {
                    verify(subscriber, atMost(HelloWorld.BYTES)).onNext(any());
                    verify(subscriber, times(1)).onComplete();
                } else {
                    verify(subscriber, times(ds[i])).onNext(any());
                    verify(subscriber, never()).onComplete();
                }
            }
        } // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
