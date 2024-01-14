package com.github.jinahya.hello._05_util_concurrent;

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
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

@DisplayName("HelloWorldFlow.HelloWorldPublisher.OfBytes")
@Slf4j
class HelloWorldFlow_01_HelloWorldPublisher_OfBytes_Test extends _HelloWorldTest {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("bytes-publisher-", 0L).factory()
    );

    @AfterAll
    static void closeExecutor() throws InterruptedException {
        EXECUTOR.shutdown();
        final var terminated = EXECUTOR.awaitTermination(4L, TimeUnit.SECONDS);
        Assertions.assertTrue(terminated, "not terminated");
        EXECUTOR.close();
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    if (buffer != null) {
                        while (buffer.hasRemaining()) {
                            buffer.put((byte) buffer.position());
                        }
                    }
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.any(ByteBuffer.class));
        final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfByte(service(), EXECUTOR);
        final var subscriber = new HelloWorldFlow.HelloWorldSubscriber.OfByte() {
            @Override
            public void onSubscribe(final Flow.Subscription subscription) {
                super.onSubscribe(subscription);
                subscription.request(HelloWorld.BYTES);
            }
        };
        publisher.subscribe(subscriber);
    }
}
