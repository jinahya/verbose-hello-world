package com.github.jinahya.hello.api;

import io.helidon.common.reactive.Multi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * <strong>Note</strong>: {@code __cancel} is omitted — Helidon Common Reactive (4.4.1) does not
 * ship a {@code TestSubscriber}, and the {@code Multi.create(Flow.Publisher)} path adds prefetch
 * buffering similar to Mutiny, so a meaningful concurrent-cancel scenario is not expressible via
 * the library's idiomatic operators.
 */
@Slf4j
class HelloWorld_Byte_FlowPublisher_Helidon_Test
        extends HelloWorld__FlowPublisher__Test<Flow.Publisher<Byte>, Byte> {

    HelloWorld_Byte_FlowPublisher_Helidon_Test() {
        super(HelloWorldFlow::ofBytes);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString().substring(getClass().getPackageName().length() + 1);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("collectList().get() → 12 elements + onComplete")
    void __collectAll() throws Exception {
        // ------------------------------------------------------------------------------ given/when
        final var list = Multi.create(publisher())
                .collectList()
                .get(10L, TimeUnit.SECONDS);
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }

    @Test
    @DisplayName("limit(n>12).collectList().get() → exactly 12 elements + onComplete")
    void __takeMoreThan12() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        // ------------------------------------------------------------------------------------ when
        final var list = Multi.create(publisher())
                .limit(n)
                .collectList()
                .get(10L, TimeUnit.SECONDS);
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }
}
