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
 * <strong>Note</strong>: {@code __cancel} is omitted — without {@code akka-stream-testkit_3} on
 * the classpath there is no idiomatic way to drive explicit per-item demand and cancel from two
 * threads via Akka Streams' public API. Cancel-via-KillSwitch races against the publisher's
 * 12-item natural completion, so a meaningful concurrent-cancel scenario is not expressible.
 */
@Slf4j
class ReactiveHelloWorld_Byte_Publisher_Akka_Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldBytePublisher, Byte> {

    private static ActorSystem system;

    @BeforeAll
    static void setUpSystem() {
        system = ActorSystem.create("ReactiveHelloWorldBytePublisher_Akka_Test");
    }

    @AfterAll
    static void shutDownSystem() {
        system.terminate();
        system.getWhenTerminated().toCompletableFuture().orTimeout(10L, TimeUnit.SECONDS).join();
    }

    ReactiveHelloWorld_Byte_Publisher_Akka_Test() {
        super(ReactiveHelloWorldBytePublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("runWith(Sink.seq()) → 12 elements + onComplete")
    void __collectAll() {
        // ------------------------------------------------------------------------------ given/when
        final var list = Source.fromPublisher(publisher())
                .runWith(Sink.<Byte>seq(), system)
                .toCompletableFuture()
                .orTimeout(10L, TimeUnit.SECONDS)
                .join();
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }

    @Test
    @DisplayName("take(n>12).runWith(Sink.seq()) → exactly 12 elements + onComplete")
    void __takeMoreThan12() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        // ------------------------------------------------------------------------------------ when
        final var list = Source.fromPublisher(publisher())
                .take(n)
                .runWith(Sink.<Byte>seq(), system)
                .toCompletableFuture()
                .orTimeout(10L, TimeUnit.SECONDS)
                .join();
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }
}
