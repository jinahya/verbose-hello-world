package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A hello world provider for {@link HelloWorldDemo}.
 */
public class HelloWorldProviderDemo implements HelloWorldProvider {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * {@inheritDoc} The {@code isAvailable} method of {@code HelloWorldProviderDemo} class returns a random boolean.
     *
     * @return a random boolean.
     */
    @Override
    public boolean isAvailable() {
        return current().nextBoolean();
    }

    @Override
    public HelloWorld getAvailable() {
        return new HelloWorldDemo();
    }
}
