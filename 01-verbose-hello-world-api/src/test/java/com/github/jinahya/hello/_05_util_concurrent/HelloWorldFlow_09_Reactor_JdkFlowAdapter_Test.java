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
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DisplayName("JdkFlowAdapter")
@Slf4j
class HelloWorldFlow_09_Reactor_JdkFlowAdapter_Test extends _HelloWorldTest {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("publisher-", 0L).factory()
    );

    @AfterAll
    static void closeTheExecutor() {
        EXECUTOR.close();
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() {
        putBuffer_willReturnTheBuffer(b -> {
            if (b == null || b.remaining() < HelloWorld.BYTES) {
                return;
            }
            b.put("hello, world".getBytes(StandardCharsets.US_ASCII)).clear();
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

//    @DisplayName("flowPublisherToFlux(publisher)")
//    @Nested
//    class FlowPublisherToFluxTest {
//
//        @Test
//        void _take_bytes() {
//            final var publisher = HelloWorldFlow.newPublisherForBytes(service(), EXECUTOR);
//            JdkFlowAdapter
//                    .flowPublisherToFlux(publisher)
//                    .doOnNext(v -> log.debug("value: {}", (char) v.byteValue()))
//                    .take(HelloWorld.BYTES)
//                    .blockLast();
//        }
//
//        @Test
//        void _reduce_bytes() {
//            final var publisher = HelloWorldFlow.newPublisherForBytes(service(), EXECUTOR);
//            final var reduced = JdkFlowAdapter
//                    .flowPublisherToFlux(publisher)
//                    .reduce(new StringBuilder(), (b, v) -> b.append((char) v.byteValue()))
//                    .block();
//            log.debug("reduced: {}", reduced);
//        }
//
//        @Test
//        void __arrays() {
//            final var publisher = HelloWorldFlow.newPublisherForArrays(service(), EXECUTOR);
//            JdkFlowAdapter
//                    .flowPublisherToFlux(publisher)
//                    .map(v -> new String(v, StandardCharsets.US_ASCII))
//                    .doOnNext(v -> log.debug("value: {}", v))
//                    .take(HelloWorld.BYTES)
//                    .blockLast();
//        }
//
//        @Test
//        void __buffers() {
//            final var publisher = HelloWorldFlow.newPublisherForBuffers(service(), EXECUTOR);
//            JdkFlowAdapter
//                    .flowPublisherToFlux(publisher)
//                    .map(StandardCharsets.US_ASCII::decode)
//                    .doOnNext(v -> log.debug("value: {}", v))
//                    .take(HelloWorld.BYTES)
//                    .blockLast();
//        }
//    }
}
