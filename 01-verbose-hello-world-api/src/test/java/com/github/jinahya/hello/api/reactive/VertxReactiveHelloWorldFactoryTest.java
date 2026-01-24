package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;

/**
 * Test class for {@link VertxReactiveHelloWorldFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("VertxReactiveHelloWorldFactory")
@Slf4j
class VertxReactiveHelloWorldFactoryTest
        extends ReactiveHelloWorldFactoryTest<VertxReactiveHelloWorldFactory> {

    // ---------------------------------------------------------------------------------------------

    VertxReactiveHelloWorldFactoryTest() {
        super(VertxReactiveHelloWorldFactory.class);
    }

    // ---------------------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        vertx = Vertx.vertx();
    }

    @AfterEach
    void tearDown() {
        if (vertx != null) {
            vertx.close();
        }
    }

    @Override
    protected VertxReactiveHelloWorldFactory newFactoryInstance() {
        return new VertxReactiveHelloWorldFactory(service, vertx);
    }

    // ---------------------------------------------------------------------------------------------
    private Vertx vertx;

    private final HelloWorld service = new HelloWorld() {
        @Override
        public byte[] set(final byte[] array, final int index) {
            final var src = "hello, world".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, array, index, src.length);
            return array;
        }
    };
}
