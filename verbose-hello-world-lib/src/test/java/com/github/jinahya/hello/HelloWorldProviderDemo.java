package com.github.jinahya.hello;

import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A hello world provider for {@link HelloWorldDemo}.
 */
public class HelloWorldProviderDemo implements HelloWorldProvider {

    private static final Logger logger = getLogger(lookup().lookupClass());

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
    public HelloWorld getInstance() {
        return new HelloWorldDemo();
    }
}
