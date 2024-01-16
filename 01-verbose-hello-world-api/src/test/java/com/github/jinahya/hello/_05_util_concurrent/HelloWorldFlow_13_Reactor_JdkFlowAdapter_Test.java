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
import com.github.jinahya.hello._HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.adapter.JdkFlowAdapter;

import java.nio.charset.StandardCharsets;

@DisplayName("JdkFlowAdapter")
@Slf4j
class HelloWorldFlow_13_Reactor_JdkFlowAdapter_Test extends _HelloWorldFlowTest {

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName("flowPublisherToFlux(publisher)")
    @Nested
    class FlowPublisherToFluxTest {

        @Test
        void _byte() {
            final var service = service();
            final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR);
            final var disposable = JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .doOnNext(v -> log.debug("byte: {}", (char) v.byteValue()))
                    .take(HelloWorld.BYTES)
                    .reduce(new StringBuilder(), (b, v) -> b.append((char) v.byteValue()))
                    .doOnNext(v -> log.debug("reduced: {}", v))
                    .subscribe();
            _HelloWorldTestUtils.awaitForOneSecond();
            disposable.dispose();
        }

        @Test
        void _array() {
            final var service = service();
            final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfArray(service, EXECUTOR);
            final var disposable = JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .doOnNext(a -> log.debug("array: {}", a))
                    .map(a -> new String(a, StandardCharsets.US_ASCII))
                    .doOnNext(s -> log.debug("string: {}", s))
                    .take(HelloWorld.BYTES)
                    .subscribe();
            _HelloWorldTestUtils.awaitForOneSecond();
            disposable.dispose();
        }

        @Test
        void _buffer() {
            final var service = service();
            final var publisher =
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR);
            final var disposable = JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .doOnNext(b -> log.debug("buffer: {}", b))
                    .map(b -> StandardCharsets.US_ASCII.decode(b).toString())
                    .doOnNext(s -> log.debug("string: {}", s))
                    .take(HelloWorld.BYTES)
                    .subscribe();
            _HelloWorldTestUtils.awaitForOneSecond();
            disposable.dispose();
        }

        @Test
        void _string() {
            final var service = service();
            final var publisher =
                    new HelloWorldFlow.HelloWorldPublisher.OfString(service, EXECUTOR);
            final var disposable = JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .doOnNext(v -> log.debug("string: {}", v))
                    .take(HelloWorld.BYTES)
                    .subscribe();
            _HelloWorldTestUtils.awaitForOneSecond();
            disposable.dispose();
        }
    }
}
