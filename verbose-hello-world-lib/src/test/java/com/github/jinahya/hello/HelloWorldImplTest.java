package com.github.jinahya.hello;

/**
 * A class for unit-testing {@link HelloWorldImpl} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldImplTest extends AbstractHelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc} The {@code helloWorld()} method of {@code HelloWorldImplTest} class returns a new instance of
     * {@link HelloWorldImpl} class.
     *
     * @return {@inheritDoc}
     */
    @Override
    HelloWorld helloWorld() {
        return new HelloWorldImpl();
    }
}
