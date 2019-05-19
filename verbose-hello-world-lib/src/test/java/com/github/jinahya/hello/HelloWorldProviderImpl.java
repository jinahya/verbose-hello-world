package com.github.jinahya.hello;

import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * An implementation of {@link HelloWorldProvider} for providing instances of {@link HelloWorldImpl}.
 */
public class HelloWorldProviderImpl implements HelloWorldProvider {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @Override
    public HelloWorld getInstance() {
        return new HelloWorldImpl();
    }
}
