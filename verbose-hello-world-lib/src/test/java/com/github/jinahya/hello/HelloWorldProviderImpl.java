package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * An implementation of {@link HelloWorldProvider} for providing instances of {@link HelloWorldImpl}.
 */
public class HelloWorldProviderImpl implements HelloWorldProvider {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public HelloWorld getAvailable() {
        return new HelloWorldImpl();
    }
}
