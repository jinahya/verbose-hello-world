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
import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DisplayName("JdkFlowAdapter")
@Slf4j
class HelloWorldFlow_09_Munity_MultiCreate_Test extends _HelloWorldTest {

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
    @DisplayName("publisher(Flow.Publisher)")
    @Nested
    class PublisherTest {

        @BeforeEach
        void subPutBuffer() {
            BDDMockito.willAnswer(i -> {
                final var buffer = i.getArgument(0, ByteBuffer.class);
                if (buffer != null && buffer.remaining() >= HelloWorld.BYTES) {
                    buffer.put("hello, world".getBytes(StandardCharsets.US_ASCII));
                }
                return buffer;
            }).given(service()).put(ArgumentMatchers.any(ByteBuffer.class));
        }

        @Test
        void __() {
            final var service = service();
            final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR);
            final var multi = Multi.createFrom().publisher(publisher);
            final var cancellable = multi
                    .log()
                    .subscribe()
                    .with(i -> {
                        log.debug("item: {}", (char) i.byteValue());
                    });
        }
    }
}
