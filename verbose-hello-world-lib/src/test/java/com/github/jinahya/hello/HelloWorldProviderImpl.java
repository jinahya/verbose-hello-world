package com.github.jinahya.hello;

/**
 * An implementation of {@link HelloWorldProvider} for providing instances of {@link HelloWorldImpl}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldProviderImpl implements HelloWorldProvider {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public HelloWorld getInstance() {
        return new HelloWorldImpl();
    }
}
