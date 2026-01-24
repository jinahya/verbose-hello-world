package com.github.jinahya.hello.api.reactive;

import akka.actor.ActorSystem;
import com.github.jinahya.hello.api.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;

/**
 * Test class for {@link AkkaReactiveHelloWorldFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("AkkaReactiveHelloWorldFactory")
@Slf4j
class AkkaReactiveHelloWorldFactoryTest
        extends ReactiveHelloWorldFactoryTest<AkkaReactiveHelloWorldFactory> {

    // ---------------------------------------------------------------------------------------------

    AkkaReactiveHelloWorldFactoryTest() {
        super(AkkaReactiveHelloWorldFactory.class);
    }

    // ---------------------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        actorSystem = ActorSystem.create("test-system");
    }

    @AfterEach
    void tearDown() {
        if (actorSystem != null) {
            actorSystem.terminate();
        }
    }

    @Override
    protected AkkaReactiveHelloWorldFactory newFactoryInstance() {
        return new AkkaReactiveHelloWorldFactory(service, actorSystem);
    }

    // ---------------------------------------------------------------------------------------------
    private ActorSystem actorSystem;

    private final HelloWorld service = new HelloWorld() {
        @Override
        public byte[] set(final byte[] array, final int index) {
            final var src = "hello, world".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, array, index, src.length);
            return array;
        }
    };
}
