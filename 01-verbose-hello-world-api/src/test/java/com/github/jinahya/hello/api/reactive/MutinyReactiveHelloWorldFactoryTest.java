package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;

/**
 * Test class for {@link MutinyReactiveHelloWorldFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("MutinyReactiveHelloWorldFactory")
@Slf4j
class MutinyReactiveHelloWorldFactoryTest
        extends ReactiveHelloWorldFactoryTest<MutinyReactiveHelloWorldFactory> {

    // ---------------------------------------------------------------------------------------------

    MutinyReactiveHelloWorldFactoryTest() {
        super(MutinyReactiveHelloWorldFactory.class);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected MutinyReactiveHelloWorldFactory newFactoryInstance() {
        return new MutinyReactiveHelloWorldFactory(service);
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service = new HelloWorld() {
        @Override
        public byte[] set(final byte[] array, final int index) {
            final var src = "hello, world".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(src, 0, array, index, src.length);
            return array;
        }
    };
}
