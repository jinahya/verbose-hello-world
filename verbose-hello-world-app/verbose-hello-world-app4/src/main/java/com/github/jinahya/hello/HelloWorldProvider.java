package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import static java.util.ServiceLoader.load;

/**
 * A class for producing (and disposing) instances of {@link HelloWorld}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldProvider {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld}.
     *
     * @return an instance of {@link HelloWorld}.
     */
    @Produces
    public HelloWorld provideHelloWorld() {
        final HelloWorld helloWorld = load(HelloWorld.class).iterator().next();
//        log.debug("producing {}", helloWorld);
        return helloWorld;
    }

    /**
     * Disposes specified instance of {@link HelloWorld}.
     *
     * @param helloWorld the instance of {@link HelloWorld} to dispose.
     */
    void disposeHelloWorld(@Disposes final HelloWorld helloWorld) {
//        log.debug("disposing {}", helloWorld);
    }
}
