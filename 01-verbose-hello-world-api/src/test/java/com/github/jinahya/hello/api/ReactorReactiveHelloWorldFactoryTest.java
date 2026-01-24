package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;

/**
 * Test class for {@link ReactorReactiveHelloWorldFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("ReactorReactiveHelloWorldFactory")
@Slf4j
class ReactorReactiveHelloWorldFactoryTest
        extends ReactiveHelloWorldFactoryTest<ReactorReactiveHelloWorldFactory> {

    // ---------------------------------------------------------------------------------------------

    ReactorReactiveHelloWorldFactoryTest() {
        super(ReactorReactiveHelloWorldFactory.class);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected ReactorReactiveHelloWorldFactory newFactoryInstance() {
        return new ReactorReactiveHelloWorldFactory(service, scheduler());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the scheduler for the factory.
     *
     * @return the scheduler
     */
    protected Scheduler scheduler() {
        return Schedulers.immediate();
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
