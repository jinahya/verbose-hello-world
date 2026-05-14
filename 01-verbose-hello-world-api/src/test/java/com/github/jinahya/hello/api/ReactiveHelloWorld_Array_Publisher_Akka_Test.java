package com.github.jinahya.hello.api;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * <strong>Note</strong>: {@code __cancel} is omitted — see
 * {@link ReactiveHelloWorld_Byte_Publisher_Akka_Test the Byte variant} for the rationale.
 */
@Slf4j
class ReactiveHelloWorld_Array_Publisher_Akka_Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldArrayPublisher, byte[]> {

    private static ActorSystem system;

    @BeforeAll
    static void setUpSystem() {
        system = ActorSystem.create("ReactiveHelloWorldArrayPublisher_Akka_Test");
    }

    @AfterAll
    static void shutDownSystem() {
        system.terminate();
        system.getWhenTerminated().toCompletableFuture().orTimeout(10L, TimeUnit.SECONDS).join();
    }

    ReactiveHelloWorld_Array_Publisher_Akka_Test() {
        super(service -> new ReactiveHelloWorldArrayPublisher(
                new ReactiveHelloWorldBytePublisher(service)
        ));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("take(n).runWith(Sink.seq()) → exactly n elements")
    void __random() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        // ------------------------------------------------------------------------------------ when
        final var list = Source.fromPublisher(publisher())
                .take(n)
                .runWith(Sink.<byte[]>seq(), system)
                .toCompletableFuture()
                .orTimeout(10L, TimeUnit.SECONDS)
                .join();
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(n, list.size());
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        for (final var element : list) {
            Assertions.assertArrayEquals(expected, element);
        }
    }
}
