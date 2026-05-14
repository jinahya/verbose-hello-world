package com.github.jinahya.hello.api;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.testkit.javadsl.TestSink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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
        super(ReactiveHelloWorldArrayPublisher::from);
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

    @Test
    @DisplayName("TestSink.probe(): request(1) → exactly 1 element, no further signals")
    void __exactly1() {
        // ------------------------------------------------------------------------------ given/when
        final var probe = Source.fromPublisher(publisher()).runWith(TestSink.probe(system), system);
        probe.request(1L);
        final var element = probe.expectNext();
        // ------------------------------------------------------------------------------------ then
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
        probe.expectNoMessage(Duration.ofMillis(200L));
    }

    @Test
    @DisplayName("TestSink.probe(): request, expect, cancel → no further signals")
    void __cancel() {
        // ------------------------------------------------------------------------------ given/when
        final var probe = Source.fromPublisher(publisher()).runWith(TestSink.probe(system), system);
        probe.request(2L);
        probe.expectNext();
        probe.expectNext();
        probe.cancel();
        // ------------------------------------------------------------------------------------ then
        probe.expectNoMessage(Duration.ofMillis(200L));
    }
}
