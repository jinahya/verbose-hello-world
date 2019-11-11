package com.github.jinahya.hello;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A hello world provider for {@link HelloWorldDemo}.
 */
public class HelloWorldProviderDemo implements HelloWorldProvider {

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
