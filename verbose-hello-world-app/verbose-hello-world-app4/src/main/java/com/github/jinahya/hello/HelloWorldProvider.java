package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import static java.util.ServiceLoader.load;

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
        return load(HelloWorld.class).iterator().next();
    }

    /**
     * Disposes specified instance of {@link HelloWorld}.
     *
     * @param helloWorld the instance of {@link HelloWorld} to dispose.
     */
    void disposeHelloWorld(@Disposes final HelloWorld helloWorld) {
        // empty
    }
}
