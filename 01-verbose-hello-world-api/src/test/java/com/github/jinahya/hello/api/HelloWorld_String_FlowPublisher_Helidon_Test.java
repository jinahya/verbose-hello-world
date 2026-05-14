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
 * <strong>Note</strong>: {@code __cancel} is omitted — see
 * {@link HelloWorld_Byte_FlowPublisher_Helidon_Test the Byte variant} for the rationale.
 */
@Slf4j
class HelloWorld_String_FlowPublisher_Helidon_Test
        extends HelloWorld__FlowPublisher__Test<Flow.Publisher<String>, String> {

    HelloWorld_String_FlowPublisher_Helidon_Test() {
        super(HelloWorldFlow::ofStrings);
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
    @DisplayName("limit(n).collectList().get() → exactly n elements")
    void __random() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        // ------------------------------------------------------------------------------------ when
        final var list = Multi.create(publisher())
                .limit(n)
                .collectList()
                .get(10L, TimeUnit.SECONDS);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(n, list.size());
        final var expected = HelloWorldTestUtils.hello_world_string();
        for (final var element : list) {
            Assertions.assertEquals(expected, element);
        }
    }
}
