package com.github.jinahya.hello._05_util_concurrent;

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.adapter.JdkFlowAdapter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DisplayName("JdkFlowAdapter")
@Slf4j
class HelloWorldFlow_03_Reactor_JdkFlowAdapter_Test extends _HelloWorldTest {

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
    @DisplayName("flowPublisherToFlux(publisher)")
    @Nested
    class FlowPublisherToFluxTest {

        @Test
        void _take_bytes() {
            final var publisher = HelloWorldFlow.newPublisherForBytes(service(), EXECUTOR);
            JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .doOnNext(v -> log.debug("value: {}", (char) v.byteValue()))
                    .take(HelloWorld.BYTES)
                    .blockLast();
        }

        @Test
        void _reduce_bytes() {
            final var publisher = HelloWorldFlow.newPublisherForBytes(service(), EXECUTOR);
            final var reduced = JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .reduce(new StringBuilder(), (b, v) -> b.append((char) v.byteValue()))
                    .block();
            log.debug("reduced: {}", reduced);
        }

        @Test
        void __arrays() {
            final var publisher = HelloWorldFlow.newPublisherForArrays(service(), EXECUTOR);
            JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .map(v -> new String(v, StandardCharsets.US_ASCII))
                    .doOnNext(v -> log.debug("value: {}", v))
                    .take(HelloWorld.BYTES)
                    .blockLast();
        }

        @Test
        void __buffers() {
            final var publisher = HelloWorldFlow.newPublisherForBuffers(service(), EXECUTOR);
            JdkFlowAdapter
                    .flowPublisherToFlux(publisher)
                    .map(StandardCharsets.US_ASCII::decode)
                    .doOnNext(v -> log.debug("value: {}", v))
                    .take(HelloWorld.BYTES)
                    .blockLast();
        }
    }
}
