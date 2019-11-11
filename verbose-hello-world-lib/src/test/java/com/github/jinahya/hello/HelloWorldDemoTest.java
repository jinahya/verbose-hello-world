package com.github.jinahya.hello;

/**
 * A class for unit-testing {@link HelloWorldDemo} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldDemoTest extends AbstractHelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc} The {@code helloWorld()} method of {@code HelloWorldImplTest} class returns a new instance of
     * {@link HelloWorldDemo} class.
     *
     * @return {@inheritDoc}
     */
    @Override
    HelloWorld helloWorld() {
        return new HelloWorldDemo();
    }
}
