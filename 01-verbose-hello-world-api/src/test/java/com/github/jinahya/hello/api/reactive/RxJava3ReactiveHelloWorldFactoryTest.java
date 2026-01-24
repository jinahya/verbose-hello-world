package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;

/**
 * Test class for {@link RxJava3ReactiveHelloWorldFactory}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("RxJava3ReactiveHelloWorldFactory")
@Slf4j
class RxJava3ReactiveHelloWorldFactoryTest
        extends ReactiveHelloWorldFactoryTest<RxJava3ReactiveHelloWorldFactory> {

    // ---------------------------------------------------------------------------------------------

    RxJava3ReactiveHelloWorldFactoryTest() {
        super(RxJava3ReactiveHelloWorldFactory.class);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected RxJava3ReactiveHelloWorldFactory newFactoryInstance() {
        return new RxJava3ReactiveHelloWorldFactory(service, scheduler());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the scheduler for the factory.
     *
     * @return the scheduler
     */
    protected Scheduler scheduler() {
        return Schedulers.trampoline();
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
