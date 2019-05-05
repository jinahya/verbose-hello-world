package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A hello world provider for {@link HelloWorldDemo}.
 */
class HelloWorldProviderDemo implements HelloWorldProvider {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * {@inheritDoc} The {@code provideHelloWorld} method of {@link HelloWorldProviderDemo} class randomly returns an
     * empty optional of {@link HelloWorldDemo}.
     *
     * @return a randomly empty optional of {@link HelloWorldDemo}.
     */
    @Override
    public Optional<HelloWorld> provideHelloWorld() {
        if (current().nextBoolean()) {
            logger.debug("returning an empty...");
            return empty();
        }
        return of(new HelloWorldDemo());
    }
}
